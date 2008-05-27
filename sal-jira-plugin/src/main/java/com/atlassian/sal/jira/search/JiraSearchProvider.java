package com.atlassian.sal.jira.search;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.search.SearchContext;
import com.atlassian.jira.issue.search.SearchContextImpl;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchProvider;
import com.atlassian.jira.issue.search.SearchRequest;
import com.atlassian.jira.issue.search.SearchRequestManager;
import com.atlassian.jira.issue.search.managers.IssueSearcherManager;
import com.atlassian.jira.issue.search.searchers.IssueSearcher;
import com.atlassian.jira.issue.search.util.QueryCreator;
import com.atlassian.jira.issue.transport.FieldValuesHolder;
import com.atlassian.jira.issue.transport.impl.FieldValuesHolderImpl;
import com.atlassian.jira.issue.transport.impl.IssueNavigatorActionParams;
import com.atlassian.jira.util.ErrorCollection;
import com.atlassian.jira.util.SimpleErrorCollection;
import com.atlassian.jira.util.searchers.ThreadLocalSearcherCache;
import com.atlassian.jira.web.bean.I18nBean;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.project.Project;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.component.ComponentLocator;
import com.atlassian.sal.api.message.DefaultMessage;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.search.BasicResourceType;
import com.atlassian.sal.api.search.BasicSearchMatch;
import com.atlassian.sal.api.search.SearchMatch;
import com.atlassian.sal.api.search.SearchResults;
import com.atlassian.sal.api.search.parameter.SearchParameter;
import com.atlassian.sal.api.search.query.DefaultQueryParser;
import com.atlassian.sal.api.search.query.QueryParser;
import com.opensymphony.user.EntityNotFoundException;
import com.opensymphony.user.User;
import com.opensymphony.user.UserManager;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.io.IOException;

/**
 *
 */
public class JiraSearchProvider implements com.atlassian.sal.api.search.SearchProvider
{
    private static final Logger log = Logger.getLogger(JiraSearchProvider.class);
    private IssueSearcherManager issueManager;
    private final QueryCreator queryCreator;
    private final SearchRequestManager searchRequestManager;
    private final SearchProvider searchProvider;
    private final UserManager userManager;
    private final ProjectManager projectManager;

    public JiraSearchProvider(IssueSearcherManager issueManager,
                              QueryCreator queryCreator, SearchRequestManager searchRequestManager,
                              com.atlassian.jira.issue.search.SearchProvider searchProvider,
                              UserManager userManager, ProjectManager projectManager)
    {
        this.issueManager = issueManager;
        this.queryCreator = queryCreator;
        this.searchRequestManager = searchRequestManager;
        this.searchProvider = searchProvider;
        this.userManager = userManager;
        this.projectManager = projectManager;
    }

    public SearchResults search(String username, String searchQuery)
    {
        QueryParser queryParser = new DefaultQueryParser(searchQuery);

        int maxHits = queryParser.getMaxHits();        
        if (maxHits == -1)
        {
            maxHits = Integer.MAX_VALUE;
        }
        final User remoteUser = getUser(username);
        if (remoteUser == null)
        {
            log.info("User '" + username + "' not found. Running anonymous search...");
        }
        final ErrorCollection errors = new SimpleErrorCollection();
        SearchRequest searchRequest = createSearchRequest(queryParser, errors, remoteUser);
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

        return performSearch(maxHits, searchRequest, remoteUser);
    }

    private SearchResults performSearch(int maxHits, SearchRequest searchRequest, User remoteUser)
    {
        try
        {
            long startTime = System.currentTimeMillis();
            final PagerFilter pagerFilter = new PagerFilter();

            pagerFilter.setMax(maxHits);
            com.atlassian.jira.issue.search.SearchResults searchResults =
                    searchProvider.search(searchRequest, remoteUser, pagerFilter);
            return new SearchResults(transformResults(searchResults), searchResults.getTotal(),
                    System.currentTimeMillis() - startTime);
        }
        catch (SearchException e)
        {
            log.error("Error executing search", e);
            ArrayList<Message> errors = new ArrayList<Message>();
            errors.add(new DefaultMessage(e.getMessage()));
            return new SearchResults(errors);
        }
        finally
        {
            try
            {
                ThreadLocalSearcherCache.resetSearchers();
            }
            catch (IOException e)
            {
                log.error("Error closing searchers", e);
            }
        }
    }

    private List<SearchMatch> transformResults(com.atlassian.jira.issue.search.SearchResults searchResults)
    {
        final List<SearchMatch> matches = new ArrayList<SearchMatch>();
        final ApplicationProperties applicationProperties = getWebProperties();
        final List issues = searchResults.getIssues();
        for (Iterator iterator = issues.iterator(); iterator.hasNext();)
        {
            final Issue issue = (Issue) iterator.next();
            matches.add(new BasicSearchMatch(applicationProperties.getBaseUrl() + "/browse/" + issue.getKey(),
                    "[" + issue.getKey() + "] " + issue.getSummary(), issue.getDescription(),
                    new BasicResourceType(applicationProperties, issue.getIssueTypeObject().getId())));
        }
        return matches;
    }

    private SearchRequest createSearchRequest(QueryParser queryParser, ErrorCollection errors, User remoteUser)
    {
        final String queryString = queryCreator.createQuery(queryParser.getSearchString());
        //TODO: Perhaps should use a non-deprecated utility here.
        Hashtable queryParams = HttpUtils.parseQueryString(queryString);

        addProjectParam(queryParser, queryParams);

        IssueNavigatorActionParams issueNavigatorActionParams = new IssueNavigatorActionParams(queryParams);
        FieldValuesHolderImpl holder = new FieldValuesHolderImpl();
        populateAndValidate(issueNavigatorActionParams, holder, errors, remoteUser);
        if (errors.hasAnyErrors())
        {
            return null;
        }

        return searchRequestManager.create(null, remoteUser, holder, getSearchContext());
    }

    private void addProjectParam(QueryParser queryParser, Hashtable queryParams)
    {
        final SearchParameter projectKey = queryParser.getParameter(SearchParameter.PROJECT);
        if(projectKey != null)
        {
            final Project project = projectManager.getProjectObjByKey(projectKey.getValue());
            if(project!=null)
            {
                queryParams.put("pid", new String[] {project.getId().toString()});
            }
        }
    }

    void populateAndValidate(IssueNavigatorActionParams actionParams, final FieldValuesHolder fieldValuesHolder,
                             ErrorCollection errors, User remoteUser)
    {
        final Collection searchers = issueManager.getAllSearchers();
        SearchContext searchContext = getSearchContext();
        for (Iterator iterator = searchers.iterator(); iterator.hasNext();)
        {
            IssueSearcher searcher = (IssueSearcher) iterator.next();
            searcher.populateFromParams(fieldValuesHolder, actionParams);
            searcher.validateParams(searchContext, fieldValuesHolder, new I18nBean(remoteUser), errors);
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
        try
        {
            return userManager.getUser(username);
        }
        catch (EntityNotFoundException e)
        {
            return null;
        }
    }
}
