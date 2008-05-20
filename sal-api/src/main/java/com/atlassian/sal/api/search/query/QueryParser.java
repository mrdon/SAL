package com.atlassian.sal.api.search.query;

import com.atlassian.sal.api.search.parameter.SearchParameter;

import java.util.Set;

/**
 * Parses a query string created by a {@link QueryCreator} to allow easy access to the various aspects of the query
 */
public interface QueryParser
{
    /**
     * Returns the original searchString entered by a user.
     *
     * @return the original searchString entered by a user.
     */
    String getSearchString();

    /**
     * Returns the maximum number of results to retrieve, if the query contained a maxHits parameter. -1,
     * if no parameter was specified.
     *
     * @return the number of maximum hits to retrieve; -1 if none was specified.
     */
    int getMaxHits();

    /**
     * Provides a set of all SearchParameters specified.
     *
     * @return set of all SearchParameters specified.
     */
    Set<SearchParameter> getParameters();

    /**
     * Returns a specific search parameter by name, or null if that parameter doesn't exist.
     *
     * @param parameterName The parameter to return
     * @return a specific search parameter by name, or null if that parameter doesn't exist.
     */
    SearchParameter getParameter(String parameterName);

    /**
     * Convenience method to return a particular parameter value directly.
     *
     * @param parameterName The parameter to return
     * @return a specific search parameter value by name, or null if that parameter doesn't exist.
     */
    String getParameterValue(String parameterName);
}
