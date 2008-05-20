package com.atlassian.sal.jira.search;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.search.*;
import com.atlassian.jira.issue.search.SearchProvider;
import com.atlassian.jira.issue.search.managers.IssueSearcherManager;
import com.atlassian.jira.issue.search.searchers.IssueSearcher;
import com.atlassian.jira.issue.search.util.QueryCreator;
import com.atlassian.jira.issue.transport.FieldValuesHolder;
import com.atlassian.jira.issue.transport.impl.FieldValuesHolderImpl;
import com.atlassian.jira.issue.transport.impl.IssueNavigatorActionParams;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.util.ErrorCollection;
import com.atlassian.jira.util.SimpleErrorCollection;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.component.ComponentLocator;
import com.atlassian.sal.api.message.DefaultMessage;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.search.BasicSearchMatch;
import com.atlassian.sal.api.search.SearchMatch;
import com.atlassian.sal.api.search.SearchResults;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpUtils;
import java.util.*;

/**
 *
 */
public class JiraSearchProvider implements com.atlassian.sal.api.search.SearchProvider
{
    private static final Logger log = Logger.getLogger(JiraSearchProvider.class);
    private IssueSearcherManager issueManager;
    private final JiraAuthenticationContext authenticationContext;
    private final QueryCreator queryCreator;
    private final SearchRequestManager searchRequestManager;
    private final SearchProvider searchProvider;

    public JiraSearchProvider(IssueSearcherManager issueManager, JiraAuthenticationContext authenticationContext,
                              QueryCreator queryCreator, SearchRequestManager searchRequestManager,
                              com.atlassian.jira.issue.search.SearchProvider searchProvider)
    {
        this.issueManager = issueManager;
        this.authenticationContext = authenticationContext;
        this.queryCreator = queryCreator;
        this.searchRequestManager = searchRequestManager;
        this.searchProvider = searchProvider;
    }

    public SearchResults search(String searchQuery)
    {
        return search(searchQuery, -1);
    }

    public SearchResults search(String searchQuery, int maxHits)
    {
        final ErrorCollection errors = new SimpleErrorCollection();
        SearchRequest searchRequest = createSearchRequest(searchQuery, errors);
        if (errors.hasAnyErrors())
        {
            final List<Message> errorMessages = new ArrayList<Message>();
            for (Iterator iterator = errors.getErrors().keySet().iterator(); iterator.hasNext();)
            {
                String key = (String) iterator.next();
                errorMessages.add(new DefaultMessage((String) errors.getErrors().get(key)));
            }
            return new SearchResults(errorMessages);
        }

        return performSearch(searchQuery, maxHits, searchRequest);
    }

    private SearchResults performSearch(String searchQuery, int maxHits, SearchRequest searchRequest)
    {
        try
        {
            long startTime = System.currentTimeMillis();
            final PagerFilter pagerFilter = new PagerFilter();

            pagerFilter.setMax(maxHits);
            com.atlassian.jira.issue.search.SearchResults searchResults =
                    searchProvider.search(searchRequest, authenticationContext.getUser(), pagerFilter);
            return new SearchResults(transformResults(searchQuery, searchResults), System.currentTimeMillis() - startTime);
        }
        catch (SearchException e)
        {
            log.error("Error executing search for '" + searchQuery + "'", e);
            ArrayList<Message> errors = new ArrayList<Message>();
            errors.add(new DefaultMessage(e.getMessage()));
            return new SearchResults(errors);
        }
    }

    private List<SearchMatch> transformResults(String searchQuery, com.atlassian.jira.issue.search.SearchResults searchResults)
    {
        final List<SearchMatch> matches = new ArrayList<SearchMatch>();
        final ApplicationProperties applicationProperties = getWebProperties();
        final List issues = searchResults.getIssues();
        for (Iterator iterator = issues.iterator(); iterator.hasNext();)
        {
            final Issue issue = (Issue) iterator.next();
            matches.add(new BasicSearchMatch(applicationProperties.getBaseUrl() + "browse/" + issue.getKey(),
                    "[" + issue.getKey() + "] " + issue.getSummary(), SearchUtils.summarize(issue.getDescription(), searchQuery),
                    new JiraResourceType(applicationProperties, issue.getIssueTypeObject().getId())));
        }
        return matches;
    }

    private SearchRequest createSearchRequest(String searchQuery, ErrorCollection errors)
    {
        final String queryString = queryCreator.createQuery(searchQuery);
        //TODO: Perhaps should use a non-deprecated utility here.
        Hashtable queryParams = HttpUtils.parseQueryString(queryString);

        IssueNavigatorActionParams issueNavigatorActionParams = new IssueNavigatorActionParams(queryParams);
        FieldValuesHolderImpl holder = new FieldValuesHolderImpl();
        populateAndValidate(issueNavigatorActionParams, holder, errors);
        if (errors.hasAnyErrors())
        {
            return null;
        }
        return searchRequestManager.create(null, authenticationContext.getUser(), holder, getSearchContext());
    }

    void populateAndValidate(IssueNavigatorActionParams actionParams, final FieldValuesHolder fieldValuesHolder, ErrorCollection errors)
    {
        final Collection searchers = issueManager.getAllSearchers();
        SearchContext searchContext = getSearchContext();
        for (Iterator iterator = searchers.iterator(); iterator.hasNext();)
        {
            IssueSearcher searcher = (IssueSearcher) iterator.next();
            searcher.populateFromParams(fieldValuesHolder, actionParams);
            searcher.validateParams(searchContext, fieldValuesHolder, authenticationContext.getI18nBean(), errors);
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
}
