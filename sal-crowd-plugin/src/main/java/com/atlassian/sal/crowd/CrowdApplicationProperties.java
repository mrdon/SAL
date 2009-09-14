package com.atlassian.sal.crowd;

import com.atlassian.config.HomeLocator;
import com.atlassian.crowd.model.application.ApplicationType;
import com.atlassian.crowd.service.soap.client.ClientProperties;
import com.atlassian.crowd.util.build.BuildUtils;
import com.atlassian.sal.api.ApplicationProperties;

import java.io.File;
import java.util.Date;

public class CrowdApplicationProperties implements ApplicationProperties
{
    private final ClientProperties clientProperties;
    private final HomeLocator homeLocator;

    public CrowdApplicationProperties(final ClientProperties clientProperties, HomeLocator homeLocator)
    {
        this.clientProperties = clientProperties;
        this.homeLocator = homeLocator;
    }

    public String getBaseUrl()
    {
        return clientProperties.getBaseURL();
    }

    public String getDisplayName()
    {
        return ApplicationType.CROWD.getDisplayName();
    }

    public String getVersion()
    {
        return BuildUtils.BUILD_VERSION;
    }

    public Date getBuildDate()
    {
        return BuildUtils.getCurrentBuildDate();
    }

    public String getBuildNumber()
    {
        return BuildUtils.BUILD_NUMBER;
    }

    public File getHomeDirectory()
    {
        String path = homeLocator.getHomePath();
        if (path != null)
        {
            return new File(path);
        }
        else
        {
            return null;
        }
    }
}
