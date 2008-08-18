package com.atlassian.sal.confluence.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import org.apache.lucene.search.Query;
import org.easymock.MockControl;
import org.easymock.classextension.MockClassControl;

import com.atlassian.confluence.core.Addressable;
import com.atlassian.confluence.search.actions.SearchBean;
import com.atlassian.confluence.search.actions.SearchQueryBean;
import com.atlassian.confluence.search.actions.SearchResultWithExcerpt;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.search.SearchResults;
import com.atlassian.sal.api.search.query.SearchQuery;
import com.atlassian.sal.api.search.query.SearchQueryParser;
import com.atlassian.sal.core.search.query.DefaultSearchQueryParser;
import com.atlassian.user.User;

/**
 *
 */
public class TestConfluenceSearchProvider extends TestCase
{
    private SearchQueryParser queryParser;
    @Override
	protected void setUp() throws Exception
	{
		super.setUp();
		queryParser = new DefaultSearchQueryParser();
	}
    public void testErrorResult()
    {
        MockControl mockQueryControl = MockClassControl.createControl(Query.class);
        final Query mockQuery = (Query) mockQueryControl.getMock();
        mockQueryControl.replay();

        MockControl mockSearchBeanControl = MockClassControl.createControl(SearchBean.class);
        SearchBean mockSearchBean = (SearchBean) mockSearchBeanControl.getMock();
        mockSearchBean.search(mockQuery);
        mockSearchBeanControl.setDefaultThrowable(new IllegalArgumentException("Strange error"));
        mockSearchBeanControl.replay();

        ConfluenceSearchProvider searchProvider = new ConfluenceSearchProvider(queryParser, null)
        {
        	@Override
            SearchQueryBean getWiredSearchQueryBean(SearchQuery searchQuery)
            {
                MockControl mockSearchQueryBeanControl = MockClassControl.createControl(SearchQueryBean.class);
                SearchQueryBean mockSearchQueryBean = (SearchQueryBean) mockSearchQueryBeanControl.getMock();
                mockSearchQueryBean.buildQuery();
                mockSearchQueryBeanControl.setReturnValue(mockQuery);
                mockSearchQueryBeanControl.replay();
                return mockSearchQueryBean;
            }

            User getUser(String username)
            {
                return null;
            }
        };
        searchProvider.setSearchBean(mockSearchBean);
        SearchResults results = searchProvider.search(null, "test");

        assertNotNull(results);
        assertEquals(1, results.getErrors().size());
        assertEquals(0, results.getMatches().size());
        assertEquals("Strange error", results.getErrors().get(0).getKey());

        mockQueryControl.verify();
        mockSearchBeanControl.verify();
    }

    public void testNoResult()
    {
        MockControl mockQueryControl = MockClassControl.createControl(Query.class);
        final Query mockQuery = (Query) mockQueryControl.getMock();
        mockQueryControl.replay();

        MockControl mockSearchBeanControl = MockClassControl.createControl(SearchBean.class);
        SearchBean mockSearchBean = (SearchBean) mockSearchBeanControl.getMock();
        mockSearchBean.search(mockQuery);
        mockSearchBeanControl.setDefaultReturnValue(Collections.EMPTY_LIST);
        mockSearchBeanControl.replay();

        ConfluenceSearchProvider searchProvider = new ConfluenceSearchProvider(queryParser, null)
        {

        	@Override
            SearchQueryBean getWiredSearchQueryBean(SearchQuery searchQuery)
            {
                MockControl mockSearchQueryBeanControl = MockClassControl.createControl(SearchQueryBean.class);
                SearchQueryBean mockSearchQueryBean = (SearchQueryBean) mockSearchQueryBeanControl.getMock();
                mockSearchQueryBean.buildQuery();
                mockSearchQueryBeanControl.setReturnValue(mockQuery);
                mockSearchQueryBeanControl.replay();
                return mockSearchQueryBean;
            }

            ApplicationProperties getApplicationProperties()
            {
                return null;
            }

            User getUser(String username)
            {
                return null;
            }
        };
        searchProvider.setSearchBean(mockSearchBean);
        SearchResults results = searchProvider.search(null, "test");

        assertNotNull(results);
        assertEquals(0, results.getErrors().size());
        assertEquals(0, results.getMatches().size());

        mockQueryControl.verify();
        mockSearchBeanControl.verify();
    }

    public void testGetResults() throws ClassNotFoundException
    {
        MockControl mockQueryControl = MockClassControl.createControl(Query.class);
        final Query mockQuery = (Query) mockQueryControl.getMock();
        mockQueryControl.replay();

        MockControl mockAddressableControl = MockControl.createControl(Addressable.class);
        Addressable mockAddressable = (Addressable) mockAddressableControl.getMock();
        mockAddressable.getType();
        mockAddressableControl.setReturnValue("page");
        mockAddressable.getUrlPath();
        mockAddressableControl.setReturnValue("/display/TST/Home");
        mockAddressable.getRealTitle();
        mockAddressableControl.setReturnValue("Home page");
        mockAddressableControl.replay();

        SearchResultWithExcerpt result = new SearchResultWithExcerpt("Some content that we're searching test for!", mockAddressable);
        List<SearchResultWithExcerpt> mockResults = new ArrayList<SearchResultWithExcerpt>();
        mockResults.add(result);


        MockControl mockSearchBeanControl = MockClassControl.createControl(SearchBean.class);
        SearchBean mockSearchBean = (SearchBean) mockSearchBeanControl.getMock();
        mockSearchBean.search(mockQuery);
        mockSearchBeanControl.setDefaultReturnValue(mockResults);
        mockSearchBeanControl.replay();

        ConfluenceSearchProvider searchProvider = new ConfluenceSearchProvider(queryParser, null)
        {

        	@Override
            SearchQueryBean getWiredSearchQueryBean(SearchQuery searchQuery)
            {
                MockControl mockSearchQueryBeanControl = MockClassControl.createControl(SearchQueryBean.class);
                SearchQueryBean mockSearchQueryBean = (SearchQueryBean) mockSearchQueryBeanControl.getMock();
                mockSearchQueryBean.buildQuery();
                mockSearchQueryBeanControl.setReturnValue(mockQuery);
                mockSearchQueryBeanControl.replay();
                return mockSearchQueryBean;
            }

            ApplicationProperties getApplicationProperties()
            {
                MockControl mockApplicationPropertiesControl = MockControl.createControl(ApplicationProperties.class);
                ApplicationProperties mockApplicationProperties = (ApplicationProperties) mockApplicationPropertiesControl.getMock();
                mockApplicationProperties.getBaseUrl();
                mockApplicationPropertiesControl.setDefaultReturnValue("http://www.atlassian.com/wiki");
                mockApplicationProperties.getApplicationName();
                mockApplicationPropertiesControl.setDefaultReturnValue("Confluence");
                mockApplicationPropertiesControl.replay();
                return mockApplicationProperties;
            }           

            User getUser(String username)
            {
                return null;
            }
        };
        searchProvider.setSearchBean(mockSearchBean);
        SearchResults results = searchProvider.search(null, "test");

        assertNotNull(results);
        assertEquals(0, results.getErrors().size());
        assertEquals(1, results.getMatches().size());
        assertEquals("Home page", results.getMatches().get(0).getTitle());
        assertEquals("http://www.atlassian.com/wiki/display/TST/Home", results.getMatches().get(0).getUrl());
        assertEquals("Some content that we're searching test for!", results.getMatches().get(0).getExcerpt());
        assertEquals("Confluence", results.getMatches().get(0).getResourceType().getName());
        assertEquals("page", results.getMatches().get(0).getResourceType().getType());
        assertEquals("http://www.atlassian.com/wiki", results.getMatches().get(0).getResourceType().getUrl());


        mockQueryControl.verify();
        mockSearchBeanControl.verify();

    }
}
