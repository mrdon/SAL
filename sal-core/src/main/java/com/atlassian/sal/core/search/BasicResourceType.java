package com.atlassian.sal.core.search;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.search.ResourceType;

/**
 */
public class BasicResourceType implements ResourceType
{
    private String name;
    private String url;
    private String type;

    public BasicResourceType(ApplicationProperties applicationProperties, String type)
    {
        this.name = applicationProperties.getDisplayName();
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
