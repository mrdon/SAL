package com.atlassian.sal.crowd.search;

import com.atlassian.sal.api.search.SearchProvider;
import com.atlassian.sal.api.search.SearchResults;
import com.atlassian.sal.api.search.SearchMatch;

import java.util.Collections;

/**
 * Search provider that always returns no search matches
 *
 * @since 2.0.0
 */
public class CrowdSearchProvider implements SearchProvider
{
    public SearchResults search(String username, String searchQuery)
    {
        return new SearchResults(Collections.<SearchMatch>emptyList(), 0, 0);
    }
}
