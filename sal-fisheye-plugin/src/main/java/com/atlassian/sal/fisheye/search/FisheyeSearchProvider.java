package com.atlassian.sal.fisheye.search;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.component.ComponentLocator;
import com.atlassian.sal.api.message.DefaultMessage;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.search.BasicSearchMatch;
import com.atlassian.sal.api.search.SearchMatch;
import com.atlassian.sal.api.search.SearchProvider;
import com.atlassian.sal.api.search.SearchResults;
import com.atlassian.sal.api.search.parameter.SearchParameter;
import com.atlassian.sal.api.search.query.DefaultQueryParser;
import com.atlassian.sal.api.search.query.QueryParser;
import com.cenqua.crucible.filters.CrucibleFilter;
import com.cenqua.crucible.model.Review;
import com.cenqua.crucible.model.managers.ReviewManager;
import com.cenqua.crucible.util.ReviewSearchTerms;
import com.cenqua.fisheye.AppConfig;
import com.cenqua.fisheye.cvsrep.search.SearchManager;
import com.cenqua.fisheye.cvsrep.search.query.FishQuery;
import com.cenqua.fisheye.rep.FileRevision;
import com.cenqua.fisheye.rep.RepositoryEngine;
import com.cenqua.fisheye.rep.RepositoryHandle;
import com.cenqua.fisheye.user.UserManager;
import com.cenqua.fisheye.util.NaturalComparator;
import com.cenqua.fisheye.web.FishEyePathInfo;
import com.cenqua.fisheye.web.SearchResultsExplorer;
import com.cenqua.fisheye.web.UrlHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 */
public class FisheyeSearchProvider implements SearchProvider
{
    private static final int MAX_FILES = 5;
    private static final String FISH_EYE = "FishEye";
    private static final String CRUCIBLE = "Crucible";

    public SearchResults search(String searchQuery)
    {
        final QueryParser queryParser = new DefaultQueryParser(searchQuery);
        final List<Message> errors = validateQuery(queryParser);
        if (!errors.isEmpty())
        {
            return new SearchResults(errors);
        }

        return search(queryParser.getSearchString(), queryParser.getMaxHits(),
                queryParser.getParameter(SearchParameter.APPLICATION),
                queryParser.getParameter(SearchParameter.PATH));
    }

    private SearchResults search(String searchQuery, int maxHits, SearchParameter applicationParam,
                                 SearchParameter pathParameter)
    {
        if (maxHits == -1)
        {
            maxHits = Integer.MAX_VALUE;
        }
        if (FISH_EYE.equals(applicationParam.getValue()))
        {
            return doFisheyeSearch(searchQuery, maxHits, pathParameter);
        }
        else if (CRUCIBLE.equals(applicationParam.getValue()))
        {
            return doCrucibleSearch(searchQuery, maxHits);
        }
        //TODO: implement global app search.
        return null;
    }

    private SearchResults doCrucibleSearch(String searchQuery, int maxHits)
    {
        long startTime = System.currentTimeMillis();
        final ReviewSearchTerms terms = new ReviewSearchTerms(searchQuery);
        final List<Integer> resultIds = new LinkedList<Integer>();
        boolean first = true;
        for (Iterator iterator = terms.getAllTerms().iterator(); iterator.hasNext();)
        {
            String term = (String) iterator.next();
            final Set resultSet = ReviewManager.searchReviewForTerm(term, "review.id", "review.id");
            if (first)
            {
                resultIds.addAll(resultSet);
                first = false;
            }
            else
            {
                resultIds.retainAll(resultSet);
            }
        }
        Collections.sort(resultIds, NaturalComparator.REVERSE_INSTANCE);

        final List<SearchMatch> matches = new ArrayList<SearchMatch>();
        final ApplicationProperties applicationProperties = ComponentLocator.getComponent(ApplicationProperties.class);
        int count = 0;
        for (Integer resultId : resultIds)
        {
            if (count++ > maxHits)
            {
                break;
            }
            final Review review = ReviewManager.getReviewById(resultId);
            final BasicSearchMatch searchMatch =
                    new BasicSearchMatch(review.getLink(), review.getName(), review.getDescription(),
                            new FisheyeResourceType(applicationProperties, "review"));
            matches.add(searchMatch);
        }
        return new SearchResults(matches, System.currentTimeMillis() - startTime);
    }

    private SearchResults doFisheyeSearch(String searchQuery, int maxHits, SearchParameter pathParameter)
    {
        final List<Message> errors = new ArrayList<Message>();
        final FishEyePathInfo pathInfo = new FishEyePathInfo(pathParameter.getValue());
        final String repositoryName = pathInfo.getRepname();
        final RepositoryEngine engine = getRepositoryEngine(repositoryName, errors);
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
            final List<SearchMatch> matches = transformResults(maxHits, repositoryName, collator);

            return new SearchResults(matches, System.currentTimeMillis() - startTime);
        }
        catch (Exception e)
        {
            errors.add(new DefaultMessage(e.getMessage()));
            return new SearchResults(errors);
        }
    }

    private List<SearchMatch> transformResults(int maxHits, String repositoryName, com.cenqua.fisheye.cvsrep.search.SearchResults collator)
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
                    firstRevision.getChangeSetId() + " by " + firstRevision.getAuthor(), excerpt, new FisheyeResourceType(applicationProperties, "changeset")));
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

    private boolean accessAllowed(RepositoryHandle h)
    {
        final UserManager um = AppConfig.getsConfig().getUserManager();
        return um.hasPermissionToAccess(um.getCurrentUser(CrucibleFilter.getRequest()), h);
    }

    private List<Message> validateQuery(QueryParser queryParser)
    {
        final List<Message> errors = new ArrayList<Message>();
        final SearchParameter applicationParameter = queryParser.getParameter(SearchParameter.APPLICATION);
        if (applicationParameter == null)
        {
            errors.add(new DefaultMessage("studio.search.errors.search.param.missing", SearchParameter.APPLICATION));
        }
        else if (!FISH_EYE.equals(applicationParameter.getValue()) && !CRUCIBLE.equals(applicationParameter.getValue()))
        {
            errors.add(new DefaultMessage("studio.search.errors.search.param.invalid.value", applicationParameter.getValue(), applicationParameter.getName()));
        }
        final SearchParameter pathParameter = queryParser.getParameter(SearchParameter.PATH);
        if (pathParameter == null && applicationParameter != null && FISH_EYE.equals(applicationParameter.getValue()))
        {
            errors.add(new DefaultMessage("studio.search.errors.search.param.missing", SearchParameter.PATH));
        }
        else
        {
            FishEyePathInfo pathInfo = new FishEyePathInfo(pathParameter.getValue());
            final String repositoryName = pathInfo.getRepname();
            getRepositoryEngine(repositoryName, errors);
        }

        return errors;
    }

    private RepositoryEngine getRepositoryEngine(String name, List<Message> errors)
    {
        try
        {
            RepositoryHandle h = AppConfig.getsConfig().getRepositoryManager().getRepository(name);

            if (h == null)
            {
                errors.add(new DefaultMessage("studio.search.errors.no.repository.found", name));
                return null;
            }

            if (!accessAllowed(h))
            {
                errors.add(new DefaultMessage("studio.search.errors.access.not.allowed.for.repository", name));
                return null;
            }

            return h.acquireEngine();
        }
        catch (RepositoryHandle.StateException e)
        {
            errors.add(new DefaultMessage("studio.search.errors.opening.repository", name));
            return null;
        }
    }
}
