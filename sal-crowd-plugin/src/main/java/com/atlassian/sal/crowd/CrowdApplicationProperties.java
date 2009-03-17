package com.atlassian.sal.crowd;

import com.atlassian.crowd.integration.service.soap.client.ClientProperties;
import com.atlassian.crowd.model.application.ApplicationType;
import com.atlassian.crowd.util.build.BuildUtils;
import com.atlassian.sal.api.ApplicationProperties;

import java.util.Date;

public class CrowdApplicationProperties implements ApplicationProperties
{
    private final ClientProperties clientProperties;

    public CrowdApplicationProperties(final ClientProperties clientProperties)
    {
        this.clientProperties = clientProperties;
    }

    public String getBaseUrl()
    {
        return clientProperties.getBaseURL();
    }

    public String getApplicationName()
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
}
