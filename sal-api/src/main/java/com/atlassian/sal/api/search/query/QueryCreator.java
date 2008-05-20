package com.atlassian.sal.api.search.query;

import com.atlassian.sal.api.search.parameter.SearchParameter;

/**
 * Utility class to help with creating a query string for the
 * {@link com.atlassian.sal.api.search.SearchProvider#search(String)} method.
 * <p/>
 * Query strings will have the form:
 * <searchString>&<param1>=<value1>&<param2>=<value2>...
 *
 * @see com.atlassian.sal.api.search.query.QueryParser
 */
public interface QueryCreator
{
    public static final String PARAMETER_SEPARATOR = "&";

    /**
     * Adds the query the user typed to the query.
     *
     * @param searchString The search string typed by the user.
     * @return a reference to this query creator.
     */
    QueryCreator addQuery(String searchString);

    /**
     * Appends an arbitrary search parameter to the query string.
     *
     * @param searchParameter the search parameter to add
     * @return a reference to this query creator
     */
    QueryCreator append(SearchParameter searchParameter);

    /**
     * Provides the queryString to use with the {@link com.atlassian.sal.api.search.SearchProvider#search(String)}
     * method.
     *
     * @return queryString created by this QueryCreator.
     */
    String queryString();
}
