package com.atlassian.sal.confluence.search;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.search.ResourceType;

/**
 */
public class ConfluenceResourceType implements ResourceType
{
    private String name;
    private String url;
    private String type;

    public ConfluenceResourceType(ApplicationProperties webProperties, String type)
    {
        this.name = webProperties.getApplicationName();
        this.url = webProperties.getBaseUrl();
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
