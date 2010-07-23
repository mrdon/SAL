package com.atlassian.sal.api.search.parameter;

/**
 * Allows to specify additional properties for a search in terms of string value pairs.  For example this could
 * specify a fixforversion=3.12 in JIRA.  Or application=crucible in Fisheye.
 *
 * @since 2.0
 */
public interface SearchParameter
{
    /**
     * The parameter for the maximum number of hits to return
     */
    public static final String MAXHITS = "maxhits";

    /**
     * The parameter for the application name to search
     */
    public static final String APPLICATION = "application";

    /**
     * The parameter for the project name to search
     */
    public static final String PROJECT = "project";

    /**
     * @return the name of the search parameter
     */
    String getName();

    /**
     * @return the value of the search parameter
     */
    String getValue();

    /**
     * Converts the parameter into a queryString suitable for a URL.
     *
     * @return querystring to add to a URL
     */
    String buildQueryString();
}
