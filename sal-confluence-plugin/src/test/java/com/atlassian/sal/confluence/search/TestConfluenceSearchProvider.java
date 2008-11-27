package com.atlassian.sal.confluence.search;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import junit.framework.TestCase;

import com.atlassian.confluence.search.service.DefaultPredefinedSearchBuilder;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.Search;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.search.SearchResults;
import com.atlassian.sal.core.search.query.DefaultSearchQueryParser;

/**
 *
 */
public class TestConfluenceSearchProvider extends TestCase
{
    public void testErrorResult() throws InvalidSearchException
    {
        final SearchManager searchManagerMock = mock(SearchManager.class);
        doThrow(new InvalidSearchException("Strange error")).when(searchManagerMock).search((Search) anyObject());

        final ConfluenceSearchProvider searchProvider = new ConfluenceSearchProvider(new DefaultPredefinedSearchBuilder(), searchManagerMock, new DefaultSearchQueryParser(), mock(UserAccessor.class), null);
        final SearchResults results = searchProvider.search(null, "test");

        assertNotNull(results);
        assertEquals(1, results.getErrors().size());
        assertEquals(0, results.getMatches().size());
        assertEquals("Strange error", results.getErrors().get(0).getKey());
    }


    public void testNoResult() throws InvalidSearchException
    {
        final SearchManager searchManagerMock = mock(SearchManager.class);
        doReturn(mock(com.atlassian.confluence.search.v2.SearchResults.class)).when(searchManagerMock).search((Search) anyObject());

        final ConfluenceSearchProvider searchProvider = new ConfluenceSearchProvider(new DefaultPredefinedSearchBuilder(), searchManagerMock, new DefaultSearchQueryParser(), mock(UserAccessor.class), null);
        final SearchResults results = searchProvider.search(null, "test");

        assertNotNull(results);
        assertEquals(0, results.getErrors().size());
        assertEquals(0, results.getMatches().size());
    }

    public void testGetResults() throws InvalidSearchException
    {
    	// one result
    	final SearchResult mockResult = mock(com.atlassian.confluence.search.v2.SearchResult.class);
    	doReturn("page").when(mockResult).getType();
    	doReturn("/display/TST/Home").when(mockResult).getUrlPath();
    	doReturn("Home page").when(mockResult).getDisplayTitle();
    	doReturn("Some content that we're searching test for!").when(mockResult).getContent();

    	// all results object
    	final com.atlassian.confluence.search.v2.SearchResults searchResultMock = mock(com.atlassian.confluence.search.v2.SearchResults.class);
    	doReturn(java.util.Arrays.asList(mockResult)).when(searchResultMock).getAll();

    	// search manager
    	final SearchManager searchManagerMock = mock(SearchManager.class);
		doReturn(searchResultMock).when(searchManagerMock).search((Search) anyObject());

		// application properties

        final ApplicationProperties applicationProperties = mock(ApplicationProperties.class);
        doReturn("http://www.atlassian.com/wiki").when(applicationProperties).getBaseUrl();
        doReturn("Confluence").when(applicationProperties).getApplicationName();

		final ConfluenceSearchProvider searchProvider = new ConfluenceSearchProvider(new DefaultPredefinedSearchBuilder(), searchManagerMock, new DefaultSearchQueryParser(), mock(UserAccessor.class), applicationProperties);
        final SearchResults results = searchProvider.search(null, "test");

        assertNotNull(results);
        assertEquals(0, results.getErrors().size());
        assertEquals(1, results.getMatches().size());
        assertEquals("Home page", results.getMatches().get(0).getTitle());
        assertEquals("http://www.atlassian.com/wiki/display/TST/Home", results.getMatches().get(0).getUrl());
        assertEquals("Some content that we're searching test for!", results.getMatches().get(0).getExcerpt());
        assertEquals("Confluence", results.getMatches().get(0).getResourceType().getName());
        assertEquals("page", results.getMatches().get(0).getResourceType().getType());
        assertEquals("http://www.atlassian.com/wiki", results.getMatches().get(0).getResourceType().getUrl());
    }
}
