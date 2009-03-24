package com.atlassian.sal.api.search;

/**
 * Defines the more information about the search resource (ie. JIRA, Wiki).
 *
 * @since 2.0
 */
public interface ResourceType
{
    /**
     * @return the search resource name
     */
    String getName();

    /**
     * @return the search resource url
     */
    String getUrl();

    /**
     * @return the search resource type
     */
    String getType();
}
