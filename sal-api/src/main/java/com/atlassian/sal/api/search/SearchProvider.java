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
     * <p/>
     * The searchQuery should be URLencoded, as it may contain parameters as well.  For example if a search should only
     * return a maximum number of hits the searchQuery would be '<searchString>&maxHits=20'
     *
     * @param searchQuery The query to run
     * @return A SearchResults object
     */
    SearchResults search(String searchQuery);
}
