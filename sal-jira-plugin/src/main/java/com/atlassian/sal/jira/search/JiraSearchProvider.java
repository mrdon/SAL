package com.atlassian.sal.jira.search;

import com.atlassian.jira.exception.DataAccessException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.search.SearchContext;
import com.atlassian.jira.issue.search.SearchContextImpl;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchProvider;
import com.atlassian.jira.issue.search.SearchRequest;
import com.atlassian.jira.issue.search.SearchRequestFactory;
import com.atlassian.jira.issue.search.managers.IssueSearcherManager;
import com.atlassian.jira.issue.search.searchers.IssueSearcher;
import com.atlassian.jira.issue.search.util.QueryCreator;
import com.atlassian.jira.issue.transport.FieldValuesHolder;
import com.atlassian.jira.issue.transport.impl.FieldValuesHolderImpl;
import com.atlassian.jira.issue.transport.impl.IssueNavigatorActionParams;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.jira.util.ErrorCollection;
import com.atlassian.jira.util.JiraKeyUtils;
import com.atlassian.jira.util.SimpleErrorCollection;
import com.atlassian.jira.util.searchers.ThreadLocalSearcherCache;
import com.atlassian.jira.web.bean.I18nBean;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.component.ComponentLocator;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.search.SearchMatch;
import com.atlassian.sal.api.search.SearchResults;
import com.atlassian.sal.api.search.parameter.SearchParameter;
import com.atlassian.sal.api.search.query.SearchQuery;
import com.atlassian.sal.api.search.query.SearchQueryParser;
import com.atlassian.sal.core.message.DefaultMessage;
import com.atlassian.sal.core.search.BasicResourceType;
import com.atlassian.sal.core.search.BasicSearchMatch;
import com.opensymphony.user.User;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import javax.servlet.http.HttpUtils;

/**
 *
 */
public class JiraSearchProvider implements com.atlassian.sal.api.search.SearchProvider
{
    private static final Logger log = Logger.getLogger(JiraSearchProvider.class);
    private final IssueSearcherManager issueSearcherManager;
    private final QueryCreator queryCreator;
    private final SearchProvider searchProvider;
    private final ProjectManager projectManager;
    private final IssueManager issueManager;
    private final SearchRequestFactory searchRequestFactory;
	private final JiraAuthenticationContext authenticationContext;
    private final UserUtil userUtil;
    private final SearchQueryParser searchQueryParser;


    public JiraSearchProvider(IssueSearcherManager issueSearcherManager,
        QueryCreator queryCreator,
        com.atlassian.jira.issue.search.SearchProvider searchProvider,
        ProjectManager projectManager, IssueManager issueManager,
        SearchRequestFactory searchRequestFactory, JiraAuthenticationContext authenticationContext, UserUtil userUtil,
        SearchQueryParser searchQueryParser)
    {
        this.issueSearcherManager = issueSearcherManager;
        this.queryCreator = queryCreator;
        this.searchProvider = searchProvider;
        this.projectManager = projectManager;
        this.issueManager = issueManager;
        this.searchRequestFactory = searchRequestFactory;
		this.authenticationContext = authenticationContext;
        this.userUtil = userUtil;
        this.searchQueryParser = searchQueryParser;
    }

    public SearchResults search(String username, String searchString)
    {
        final SearchQuery searchQuery = searchQueryParser.parse(searchString);
        final int maxHits = searchQuery.getParameter(SearchParameter.MAXHITS, Integer.MAX_VALUE);

        final User remoteUser = getUser(username);
        final User oldAuthenticationContextUser = getAuthenticationContextUser();
        try {
        	setAuthenticationContextUser(remoteUser);
        	if (remoteUser == null)
        	{
        		log.info("User '" + username + "' not found. Running anonymous search...");
        	}
        	final ErrorCollection errors = new SimpleErrorCollection();
        	// See if the search String contains a JIRA issue
        	final Collection<Issue> issues = getIssuesFromQuery(searchQuery.getSearchString());

        	final SearchRequest searchRequest = createSearchRequest(searchQuery, errors, remoteUser);
        	if (errors.hasAnyErrors())
        	{
        		final List<Message> errorMessages = new ArrayList<Message>();
        		for (final Iterator iterator = errors.getErrors().keySet().iterator(); iterator.hasNext();)
        		{
        			final String key = (String) iterator.next();
        			errorMessages.add(new DefaultMessage((String) errors.getErrors().get(key)));
        		}
        		return new SearchResults(errorMessages);
        	}

        	return performSearch(maxHits, searchRequest, issues, remoteUser);
        } finally
        {
        	// restore original user (who is hopefully null)
        	setAuthenticationContextUser(oldAuthenticationContextUser);
        }
    }

    private SearchResults performSearch(int maxHits, SearchRequest searchRequest, Collection<Issue> issues,
       User remoteUser)
    {
        try
        {
            final long startTime = System.currentTimeMillis();
            final PagerFilter pagerFilter = new PagerFilter();

            pagerFilter.setMax(maxHits);
            final com.atlassian.jira.issue.search.SearchResults searchResults =
                searchProvider.search(searchRequest, remoteUser, pagerFilter);
            issues.addAll(searchResults.getIssues());
            final int numResults = searchResults.getTotal() - searchResults.getIssues().size()+issues.size();

            List<Issue> trimedResults = new ArrayList<Issue>(issues);
            if (trimedResults.size() > maxHits)
            	trimedResults = trimedResults.subList(0, maxHits);

			return new SearchResults(transformResults(trimedResults), numResults,
                System.currentTimeMillis() - startTime);
        }
        catch (final SearchException e)
        {
            log.error("Error executing search", e);
            final ArrayList<Message> errors = new ArrayList<Message>();
            errors.add(new DefaultMessage(e.getMessage()));
            return new SearchResults(errors);
        }
        finally
        {
            ThreadLocalSearcherCache.resetSearchers();
        }
    }

    private List<SearchMatch> transformResults(final Collection<Issue> issues)
    {
        final List<SearchMatch> matches = new ArrayList<SearchMatch>();
        final ApplicationProperties applicationProperties = getWebProperties();
        for (final Iterator iterator = issues.iterator(); iterator.hasNext();)
        {
            final Issue issue = (Issue) iterator.next();
            matches.add(new BasicSearchMatch(applicationProperties.getBaseUrl() + "/browse/" + issue.getKey(),
                "[" + issue.getKey() + "] " + issue.getSummary(), issue.getDescription(),
                new BasicResourceType(applicationProperties, issue.getIssueTypeObject().getId())));
        }
        return matches;
    }

    private SearchRequest createSearchRequest(SearchQuery query, ErrorCollection errors, User remoteUser)
    {
        final String queryString = queryCreator.createQuery(query.getSearchString());
        //TODO: Perhaps should use a non-deprecated utility here.
        final Hashtable queryParams = HttpUtils.parseQueryString(queryString);

        addProjectParam(query, queryParams);

        final IssueNavigatorActionParams issueNavigatorActionParams = new IssueNavigatorActionParams(queryParams);
        final FieldValuesHolderImpl holder = new FieldValuesHolderImpl();
        populateAndValidate(issueNavigatorActionParams, holder, errors, remoteUser);
        if (errors.hasAnyErrors())
        {
            return null;
        }

        return searchRequestFactory.create(null, remoteUser, holder, null, getSearchContext());
    }

    private Collection<Issue> getIssuesFromQuery(String query)
    {
        final Collection<String> issueKeys = getIssueKeysFromString(query);
        // Need to ensure issue order is maintained, while also ensuring uniqueness, hence LinkedHashSet
        final Collection<Issue> issues = new LinkedHashSet<Issue>();
        for (final String issueKey : issueKeys)
        {
            final Issue issue = getIssueByKey(issueKey);
            if (issue != null)
            {
                issues.add(issue);
            }
        }
        return issues;
    }

    List<String> getIssueKeysFromString(final String query) 
    {
        return JiraKeyUtils.getIssueKeysFromString(query);
    }

    private Issue getIssueByKey(String issueKey)
    {
        try
        {
            return issueManager.getIssueObject(issueKey);
        }
        catch (final DataAccessException dae)
        {
            // Not found
            return null;
        }
    }

    private void addProjectParam(SearchQuery query, Hashtable queryParams)
    {
        final String projectKey = query.getParameter(SearchParameter.PROJECT);
        if (projectKey != null)
        {
            final Project project = projectManager.getProjectObjByKey(projectKey);
            if (project != null)
            {
                queryParams.put("pid", new String[]{project.getId().toString()});
            }
        }
    }

    void populateAndValidate(IssueNavigatorActionParams actionParams, final FieldValuesHolder fieldValuesHolder,
        ErrorCollection errors, User remoteUser)
    {
        final Collection searchers = issueSearcherManager.getAllSearchers();
        final SearchContext searchContext = getSearchContext();
        for (final Iterator iterator = searchers.iterator(); iterator.hasNext();)
        {
            final IssueSearcher searcher = (IssueSearcher) iterator.next();
            searcher.getSearchInputTransformer().populateFromParams(fieldValuesHolder, actionParams);
            searcher.getSearchInputTransformer().validateParams(searchContext, fieldValuesHolder, new I18nBean(remoteUser), errors);
        }
    }

    SearchContext getSearchContext()
    {
        return new SearchContextImpl();
    }

    ApplicationProperties getWebProperties()
    {
        return ComponentLocator.getComponent(ApplicationProperties.class);
    }

    User getUser(String username)
    {
        if (StringUtils.isEmpty(username))
        {
            return null;
        }
        return userUtil.getUser(username);
    }

    void setAuthenticationContextUser(final User remoteUser)
	{
		authenticationContext.setUser(remoteUser);
	}

    User getAuthenticationContextUser()
    {
    	return authenticationContext.getUser();
    }

}
