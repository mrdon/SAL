package com.atlassian.sal.jira.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.search.*;
import com.atlassian.jira.issue.transport.FieldValuesHolder;
import com.atlassian.jira.issue.transport.impl.FieldValuesHolderImpl;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.search.SearchMatch;
import com.atlassian.sal.core.search.query.DefaultSearchQueryParser;
import com.opensymphony.user.User;

/**
 *
 */
@SuppressWarnings({"UnusedDeclaration", "JUnitTestMethodWithNoAssertions"})
public class TestJiraSearchProvider
{
    private JiraSearchProvider searchProvider;
    @Mock private SearchProvider mockSearchProvider;
    @Mock private UserUtil mockUserUtil;
    @Mock private SearchRequestFactory mockSearchRequestFactory;
    @Mock private ApplicationProperties mockApplicationProperties;
    @Mock private JiraAuthenticationContext mockAuthenticationContext;
    @Mock private IssueManager mockIssueManager;
    @Mock private ProjectManager mockProjectManager;

    private Collection<String> keys;

    @Before public void setUp() throws Exception
    {
        MockitoAnnotations.initMocks(this);

        when(mockApplicationProperties.getBaseUrl()).thenReturn("http://jira.atlassian.com");
        when(mockApplicationProperties.getApplicationName()).thenReturn("JIRA");

        keys = new ArrayList<String>();

        searchProvider = new JiraSearchProvider(mockSearchProvider, mockUserUtil, mockProjectManager,
            mockIssueManager, mockSearchRequestFactory, mockAuthenticationContext, new DefaultSearchQueryParser(),
            mockApplicationProperties)
        {
            @Override Collection<String> getIssueKeysFromQuery(String query)
            {
                return keys;
            }

            @Override SearchContext getSearchContext()
            {
                return null;
            }
        };

    }

    @Test public void searchWithNoResults() throws SearchException
    {
        when(mockSearchProvider.search((SearchRequest) isNull(), (User) isNull(), any(PagerFilter.class))).thenReturn(
            new SearchResults(Collections.EMPTY_LIST, 0, PagerFilter.getUnlimitedFilter()));

        final com.atlassian.sal.api.search.SearchResults results = searchProvider.search(null, "query");
        assertNotNull(results);
        assertEquals(0, results.getErrors().size());
        assertEquals(0, results.getMatches().size());

        verify(mockSearchRequestFactory).create(null, null, createFieldValuesHolder("query"), null);
    }

    @Test public void allWordsInSearch() throws SearchException
    {
        when(mockSearchProvider.search((SearchRequest) isNull(), (User) isNull(), any(PagerFilter.class))).thenReturn(
            new SearchResults(Collections.EMPTY_LIST, 0, PagerFilter.getUnlimitedFilter()));

        searchProvider.search(null, "search terms");

        verify(mockSearchRequestFactory).create(null, null, createFieldValuesHolder("+search +terms"), null);
    }

    @Test public void searchWithResults() throws SearchException
    {
        FieldValuesHolder fieldValuesHolder = createFieldValuesHolder("query");

        IssueType mockIssueType = mock(IssueType.class);
        when(mockIssueType.getId()).thenReturn("1");

        Issue mockIssue = mock(Issue.class);
        when(mockIssue.getKey()).thenReturn("JST-234");
        when(mockIssue.getSummary()).thenReturn("Sample Summary");
        when(mockIssue.getDescription()).thenReturn("Sample description for the query issue.");
        when(mockIssue.getIssueTypeObject()).thenReturn(mockIssueType);

        List<Issue> issues = new ArrayList<Issue>();
        issues.add(mockIssue);

        when(mockSearchProvider.search((SearchRequest) isNull(), (User) isNull(), any(PagerFilter.class))).thenReturn(
            new SearchResults(issues, 1, PagerFilter.getUnlimitedFilter()));

        final com.atlassian.sal.api.search.SearchResults results = searchProvider.search(null, "query");
        assertNotNull(results);
        assertEquals(0, results.getErrors().size());
        assertEquals(1, results.getMatches().size());
        //assert stuff abou the one match
        final SearchMatch searchMatch = results.getMatches().get(0);
        assertEquals("http://jira.atlassian.com/browse/JST-234", searchMatch.getUrl());
        assertEquals("[JST-234] Sample Summary", searchMatch.getTitle());
        assertEquals("Sample description for the query issue.", searchMatch.getExcerpt());
        assertEquals("1", searchMatch.getResourceType().getType());
        assertEquals("JIRA", searchMatch.getResourceType().getName());
        assertEquals("http://jira.atlassian.com", searchMatch.getResourceType().getUrl());

        verify(mockSearchRequestFactory).create(null, null, fieldValuesHolder, null);
    }

    @Test public void issueKeyInSearch() throws SearchException
    {
        when(mockSearchProvider.search((SearchRequest) isNull(), (User) isNull(), any(PagerFilter.class))).thenReturn(
            new SearchResults(Collections.EMPTY_LIST, 0, PagerFilter.getUnlimitedFilter()));

        keys.add("JST-234");

        IssueType mockIssueType = mock(IssueType.class);
        when(mockIssueType.getId()).thenReturn("1");

        MutableIssue mockIssue = mock(MutableIssue.class);
        when(mockIssue.getKey()).thenReturn("JST-234");
        when(mockIssue.getSummary()).thenReturn("Sample Summary");
        when(mockIssue.getDescription()).thenReturn("Sample description for the query issue.");
        when(mockIssue.getIssueTypeObject()).thenReturn(mockIssueType);

        when(mockIssueManager.getIssueObject("JST-234")).thenReturn(mockIssue);

        com.atlassian.sal.api.search.SearchResults results = searchProvider.search(null, "JST-234");

        assertNotNull(results);
        assertEquals(0, results.getErrors().size());
        assertEquals(1, results.getMatches().size());
        //assert stuff abou the one match
        final SearchMatch searchMatch = results.getMatches().get(0);
        assertEquals("http://jira.atlassian.com/browse/JST-234", searchMatch.getUrl());
    }

    @SuppressWarnings("unchecked")
    private FieldValuesHolder createFieldValuesHolder(String query)
    {
        FieldValuesHolder holder = new FieldValuesHolderImpl();
        holder.put("query", query);
        holder.put("summary", "true");
        holder.put("body", "true");
        holder.put("description", "true");
        holder.put("environment", "true");
        return holder;
    }
}
