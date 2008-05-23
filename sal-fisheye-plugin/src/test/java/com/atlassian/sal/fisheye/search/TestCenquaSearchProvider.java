package com.atlassian.sal.fisheye.search;

import com.atlassian.sal.api.search.SearchProvider;
import com.atlassian.sal.api.search.SearchResults;
import junit.framework.TestCase;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 */
public class TestCenquaSearchProvider extends TestCase
{

    public void testValidation()
    {
        final AtomicBoolean fisheyeProviderCalled = new AtomicBoolean(false);
        final AtomicBoolean crucibleProviderCalled = new AtomicBoolean(false);
        CenquaSearchProvider searchProvider = new CenquaSearchProvider()
        {
            SearchProvider getFisheyeSearchProvider()
            {
                fisheyeProviderCalled.set(true);
                return new SearchProvider()
                {

                    public SearchResults search(String username, String searchQuery)
                    {
                        //do nothing!
                        return null;
                    }
                };
            }

            SearchProvider getCrucibleSearchProvider()
            {
                crucibleProviderCalled.set(true);
                return new SearchProvider()
                {

                    public SearchResults search(String username, String searchQuery)
                    {
                        //do nothing!
                        return null;
                    }
                };
            }
        };
        SearchResults results = searchProvider.search("testuser", "query");
        assertEquals(1, results.getErrors().size());
        assertEquals("studio.search.errors.search.param.missing", results.getErrors().get(0).getKey());
        assertFalse(crucibleProviderCalled.get());
        assertFalse(fisheyeProviderCalled.get());

        results = searchProvider.search("testuser", "query&application=cruc");
        assertEquals(1, results.getErrors().size());
        assertEquals("studio.search.errors.search.param.invalid.value", results.getErrors().get(0).getKey());
        assertFalse(crucibleProviderCalled.get());
        assertFalse(fisheyeProviderCalled.get());

        results = searchProvider.search("testuser", "query&application=FishEye");
        assertTrue(fisheyeProviderCalled.get());
        assertFalse(crucibleProviderCalled.get());

        //reset the fisheye provider
        fisheyeProviderCalled.set(false);

        results = searchProvider.search("testuser", "query&application=Crucible");
        assertFalse(fisheyeProviderCalled.get());
        assertTrue(crucibleProviderCalled.get());
    }
}
