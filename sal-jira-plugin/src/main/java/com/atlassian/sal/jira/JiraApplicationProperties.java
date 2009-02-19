package com.atlassian.sal.jira;

import com.atlassian.jira.util.BuildUtils;
import com.atlassian.jira.util.velocity.VelocityRequestContextFactory;
import com.atlassian.sal.api.ApplicationProperties;

import java.util.Date;

/**
 * JIRA implementation of WebProperties
 */
public class JiraApplicationProperties implements ApplicationProperties
{
    private final VelocityRequestContextFactory velocityRequestContextFactory;

    public JiraApplicationProperties(VelocityRequestContextFactory velocityRequestContextFactory)
    {
        this.velocityRequestContextFactory = velocityRequestContextFactory;
    }

    public String getBaseUrl()
    {
        return velocityRequestContextFactory.getJiraVelocityRequestContext().getCanonicalBaseUrl();
    }

    public String getApplicationName()
    {
        return "JIRA";
    }

    public String getVersion()
    {
        return BuildUtils.getVersion();
    }

    public Date getBuildDate()
    {
        return BuildUtils.getCurrentBuildDate();
    }

    public String getBuildNumber()
    {
        return BuildUtils.getCurrentBuildNumber();
    }
}
