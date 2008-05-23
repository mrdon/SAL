package com.atlassian.sal.fisheye.search;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.component.ComponentLocator;
import com.atlassian.sal.api.message.DefaultMessage;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.search.BasicResourceType;
import com.atlassian.sal.api.search.BasicSearchMatch;
import com.atlassian.sal.api.search.SearchMatch;
import com.atlassian.sal.api.search.SearchProvider;
import com.atlassian.sal.api.search.SearchResults;
import com.atlassian.sal.api.search.parameter.BasicSearchParameter;
import com.atlassian.sal.api.search.parameter.SearchParameter;
import com.atlassian.sal.api.search.query.DefaultQueryParser;
import com.atlassian.sal.api.search.query.QueryParser;
import com.cenqua.crucible.filters.CrucibleFilter;
import com.cenqua.fisheye.AppConfig;
import com.cenqua.fisheye.LicensePolicyException;
import com.cenqua.fisheye.cvsrep.search.SearchManager;
import com.cenqua.fisheye.cvsrep.search.query.FishQuery;
import com.cenqua.fisheye.rep.DbException;
import com.cenqua.fisheye.rep.FileRevision;
import com.cenqua.fisheye.rep.RepositoryEngine;
import com.cenqua.fisheye.rep.RepositoryHandle;
import com.cenqua.fisheye.user.UserLogin;
import com.cenqua.fisheye.user.UserManager;
import com.cenqua.fisheye.web.FishEyePathInfo;
import com.cenqua.fisheye.web.SearchResultsExplorer;
import com.cenqua.fisheye.web.UrlHelper;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * A FishEye specific search provider.  Groups search results by change sets.  Only repositories that the user
 * has permission for will be returned in the search.  An optional PATH parameter can be specified to limit the search
 * to a specific repository.
 */
public class FisheyeSearchProvider implements SearchProvider
{
    private static final Logger log = Logger.getLogger(FisheyeSearchProvider.class);

    private static final int MAX_FILES = 5;

    public SearchResults search(String username, String searchQuery)
    {
        final QueryParser parser = new DefaultQueryParser(searchQuery);
        final List<Message> errors = validateQuery(parser, username);
        if (!errors.isEmpty())
        {
            return new SearchResults(errors);
        }

        int maxHits = parser.getMaxHits();
        if (maxHits == -1)
        {
            maxHits = Integer.MAX_VALUE;
        }

        return doFisheyeSearch(parser.getSearchString(), maxHits, parser.getParameter(SearchParameter.PATH), username);
    }

    private List<Message> validateQuery(QueryParser queryParser, String username)
    {
        final List<Message> errors = new ArrayList<Message>();
        //FishEye also may have a pathParameter...
        final SearchParameter pathParameter = queryParser.getParameter(SearchParameter.PATH);
        if (pathParameter != null)
        {
            final FishEyePathInfo pathInfo = new FishEyePathInfo(pathParameter.getValue());
            final String repositoryName = pathInfo.getRepname();
            if (repositoryName == null)
            {
                errors.add(new DefaultMessage("studio.search.errors.no.repository.found", pathParameter.getValue()));
            }
            else
            {
                getRepositoryEngine(repositoryName, errors, username);
            }
        }
        return errors;
    }


    private SearchResults doFisheyeSearch(String searchQuery, int maxHits, SearchParameter pathParameter, String username)
    {
        //if no path was specified, try to search all repositories!
        if (pathParameter == null)
        {
            long startTime = System.currentTimeMillis();
            List<SearchMatch> matches = new ArrayList<SearchMatch>();
            final List<Message> errors = new ArrayList<Message>();

            int combinedTotal = 0;
            final Set<String> projectKeys = getLocalProjectKeys();
            for (String projectKey : projectKeys)
            {
                final SearchResults results = doFisheyeRepositorySearch(searchQuery, maxHits,
                        new BasicSearchParameter(SearchParameter.PATH, projectKey), username);
                matches.addAll(results.getMatches());
                errors.addAll(results.getErrors());
                combinedTotal += results.getTotalResults();
            }
            if (errors.isEmpty())
            {
                //need to ensure we don't return more than maxHits results!
                if (matches.size() > maxHits)
                {
                    matches = new ArrayList<SearchMatch>(matches.subList(0, maxHits));
                }
                return new SearchResults(matches, combinedTotal, System.currentTimeMillis() - startTime);
            }
            else
            {
                return new SearchResults(errors);
            }

        }
        return doFisheyeRepositorySearch(searchQuery, maxHits, pathParameter, username);
    }

    private SearchResults doFisheyeRepositorySearch(String searchQuery, int maxHits,
                                                    SearchParameter pathParameter, String username)
    {
        final List<Message> errors = new ArrayList<Message>();
        final FishEyePathInfo pathInfo = new FishEyePathInfo(pathParameter.getValue());
        final String repositoryName = pathInfo.getRepname();
        final RepositoryEngine engine = getRepositoryEngine(repositoryName, errors, username);
        final SearchManager search = engine.getSearchManager();

        if (!errors.isEmpty())
        {
            return new SearchResults(errors);
        }

        StringBuffer query = new StringBuffer();
        query.append("select revisions where path like ").append(searchQuery).append(" or ").
                append("author = ").append(searchQuery).append(" or ").
                append("comment matches ").append(searchQuery).
                append(" group by changeset");

        final List errorStrings = new ArrayList();
        FishQuery q = FishQuery.parse(query.toString(), errorStrings);
        if (q == null)
        {
            for (Iterator iterator = errorStrings.iterator(); iterator.hasNext();)
            {
                String errorString = (String) iterator.next();
                errors.add(new DefaultMessage(errorString));
            }
            return new SearchResults(errors);
        }

        try
        {
            long startTime = System.currentTimeMillis();

            com.cenqua.fisheye.cvsrep.search.SearchResults collator = search.runQuery(q, true);
            final List<SearchMatch> matches = transformFisheyeResults(maxHits, repositoryName, collator);

            return new SearchResults(matches, collator.size(), System.currentTimeMillis() - startTime);
        }
        catch (Exception e)
        {
            errors.add(new DefaultMessage(e.getMessage()));
            return new SearchResults(errors);
        }
    }

    private List<SearchMatch> transformFisheyeResults(int maxHits, String repositoryName, com.cenqua.fisheye.cvsrep.search.SearchResults collator)
            throws Exception
    {
        final UrlHelper searchUrl = new UrlHelper();
        ApplicationProperties applicationProperties = ComponentLocator.getComponent(ApplicationProperties.class);
        final String baseUrl = applicationProperties.getBaseUrl();
        searchUrl.setUrl(baseUrl + "/search/" + repositoryName + "/");

        SearchResultsExplorer results = new SearchResultsExplorer(collator);
        results.init(CrucibleFilter.getRequest(), searchUrl);
        final List groups = results.getGroups();
        final List<SearchMatch> matches = new ArrayList<SearchMatch>();
        int count = 0;
        for (Iterator iterator = groups.iterator(); iterator.hasNext() && count < maxHits; count++)
        {
            SearchResultsExplorer.GroupItem groupItem = (SearchResultsExplorer.GroupItem) iterator.next();
            final FileRevision firstRevision = groupItem.getFirst();

            final List<FileRevision> revisionList = groupItem.getItems();
            String excerpt = buildExcerpt(baseUrl, repositoryName, firstRevision, revisionList);
            matches.add(new BasicSearchMatch(baseUrl + "/changelog/" + repositoryName + "/" + firstRevision.getPath() + "/?cs=" + firstRevision.getChangeSetId(),
                    firstRevision.getChangeSetId() + " by " + firstRevision.getAuthor(), excerpt, new BasicResourceType(applicationProperties, "changeset")));
        }
        return matches;
    }

    private String buildExcerpt(String baseUrl, String repositoryName, FileRevision firstRevision, List<FileRevision> revisionList)
    {
        StringBuffer excerpt = new StringBuffer();
        excerpt.append("<span class=\"csComment\">").append(firstRevision.getComment()).append("</span><br/>");
        int count = 0;
        for (FileRevision fileRevision : revisionList)
        {
            count++;
            excerpt.append("<a class=\"revLink\" href=\"").
                    append(baseUrl).append("/browse/").append(repositoryName).append("/").append(fileRevision.getPath()).append("\">").
                    append(fileRevision.getPath()).
                    append("</a><br/>");
            if (count > MAX_FILES)
            {
                excerpt.append("...");
                break;
            }
        }
        return excerpt.toString();
    }

    private RepositoryEngine getRepositoryEngine(String repositoryName, List<Message> errors, String username)
    {
        try
        {
            RepositoryHandle handle = AppConfig.getsConfig().getRepositoryManager().getRepository(repositoryName);

            if (handle == null)
            {
                errors.add(new DefaultMessage("studio.search.errors.no.repository.found", repositoryName));
                return null;
            }

            if (!hasPermissionToView(username, handle))
            {
                errors.add(new DefaultMessage("studio.search.errors.access.not.allowed.for.repository", repositoryName));
                return null;
            }

            return handle.acquireEngine();
        }
        catch (RepositoryHandle.StateException e)
        {
            errors.add(new DefaultMessage("studio.search.errors.opening.repository", repositoryName));
            return null;
        }
    }

    private boolean hasPermissionToView(String username, RepositoryHandle handle)
    {
        final UserManager um = AppConfig.getsConfig().getUserManager();
        UserLogin userLogin;
        if (username == null)
        {
            userLogin = null;
        }
        else
        {
            try
            {
                userLogin = um.createTrustedUserLogin(username);
            }
            catch (DbException e)
            {
                log.warn("Invalid user '" + username + "' tried to view review. Access denied.");
                return false;
            }
            catch (LicensePolicyException e)
            {
                log.warn("License exception when user '" + username + "' tried to view review. Access denied.");
                return false;
            }
        }
        return um.hasPermissionToAccess(userLogin, handle);
    }


    private Set<String> getLocalProjectKeys()
    {
        Set<String> repoData = new HashSet<String>();
        for (RepositoryHandle h : AppConfig.getsConfig().getRepositoryManager().getHandles())
        {
            repoData.add(h.getName());
        }
        return repoData;
    }
}
