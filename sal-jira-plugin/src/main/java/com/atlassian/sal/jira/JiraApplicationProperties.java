package com.atlassian.sal.jira;

import com.atlassian.jira.util.BuildUtils;
import com.atlassian.jira.util.velocity.VelocityRequestContextFactory;
import com.atlassian.jira.config.util.JiraHome;
import com.atlassian.sal.api.ApplicationProperties;

import java.util.Date;
import java.io.File;

/**
 * JIRA implementation of WebProperties
 */
public class JiraApplicationProperties implements ApplicationProperties
{
    private final VelocityRequestContextFactory velocityRequestContextFactory;
    private final JiraHome jiraHome;

    public JiraApplicationProperties(VelocityRequestContextFactory velocityRequestContextFactory, JiraHome jiraHome)
    {
        this.velocityRequestContextFactory = velocityRequestContextFactory;
        this.jiraHome = jiraHome;
    }

    public String getBaseUrl()
    {
        return velocityRequestContextFactory.getJiraVelocityRequestContext().getCanonicalBaseUrl();
    }

    public String getDisplayName()
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

    public File getHomeDirectory()
    {
        return jiraHome.getHome();
    }
}
