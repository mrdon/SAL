package com.atlassian.sal.api.search;

/**
 * Allows for simple string based searches in an application.  Currently this only supports String based
 * (quicksearch type) queries, but this may be extended in the future.
 */
public interface SearchProvider
{
    /**
     * Runs the a search given a query and returns a searchResult. The query will return as many hits as the underlying
     * application would return by default.  Use this method sparingly as it will result in a bigger performance hit.
     *
     * @param searchQuery The query to run
     * @return A SearchResults object
     */
    SearchResults search(String searchQuery);

    /**
     * Runs the a search given a query and returns a searchResult. The query will only return as many hits as specified
     * via maxHits.
     *
     * @param searchQuery The query to run
     * @param maxHits Limit the number of search hits to return
     * @return A SearchResults object
     */
    SearchResults search(String searchQuery, int maxHits);
}
