package com.atlassian.sal.jira.search;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.search.ResourceType;

/**
 */
public class JiraResourceType implements ResourceType
{
    private final String name;
    private final String url;
    private final String type;

    public JiraResourceType(ApplicationProperties applicationProperties, String type)
    {
        this.name = applicationProperties.getApplicationName();
        this.url = applicationProperties.getBaseUrl();
        this.type = type;
    }

    public String getName()
    {
        return name;
    }

    public String getUrl()
    {
        return url;
    }

    public String getType()
    {
        return type;
    }
}
