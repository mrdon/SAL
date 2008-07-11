package com.atlassian.sal.core.search;

import com.atlassian.sal.api.search.SearchMatch;
import com.atlassian.sal.api.search.ResourceType;

/**
 * Simplest implementation of a searchMatch.
 */
public class BasicSearchMatch implements SearchMatch
{
    private String url;
    private String title;
    private String excerpt;
    private ResourceType resourceType;

    public BasicSearchMatch(String url, String title, String excerpt, ResourceType resourceType)
    {
        this.url = url;
        this.title = title;
        this.excerpt = excerpt;
        this.resourceType = resourceType;
    }

    public String getUrl()
    {
        return url;
    }

    public String getTitle()
    {
        return title;
    }

    public String getExcerpt()
    {
        return excerpt;
    }

    public ResourceType getResourceType()
    {
        return resourceType;
    }

}
