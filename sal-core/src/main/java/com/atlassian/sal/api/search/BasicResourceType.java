package com.atlassian.sal.api.search;

import com.atlassian.sal.api.ApplicationProperties;

/**
 */
public class BasicResourceType implements ResourceType
{
    private String name;
    private String url;
    private String type;

    public BasicResourceType(ApplicationProperties applicationProperties, String type)
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
