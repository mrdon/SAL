package com.atlassian.sal.api.search.parameter;

/**
 * Allows to specify additional properties for a search in terms of string value pairs.  For example this could
 * specify a fixforversion=3.12 in JIRA.  Or application=crucible in Fisheye.
 */
public interface SearchParameter
{
    public static final String MAXHITS = "maxhits";
    public static final String APPLICATION = "application";
    public static final String PROJECT = "project";

    String getName();

    String getValue();

    /**
     * Converts the parameter into a queryString suitable for a URL.
     * @return querystring to add to a URL
     */
    String getQueryString();
}
