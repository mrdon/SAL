package com.atlassian.sal.fisheye.search;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.search.ResourceType;

/**
 *
 */
public class FisheyeResourceType implements ResourceType
{
    private String name;
    private String url;
    private String type;

    public FisheyeResourceType(ApplicationProperties applicationProperties, String type)
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
