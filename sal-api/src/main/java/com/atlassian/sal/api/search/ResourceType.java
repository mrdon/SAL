package com.atlassian.sal.api.search;

/**
 * Defines the more information about the search resource (ie. JIRA, Wiki).
 */
public interface ResourceType
{
    String getName();

    String getUrl();

    String getType();
}
