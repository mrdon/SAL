package com.atlassian.sal.jira;

import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.sal.api.ApplicationProperties;

/**
 * JIRA implementation of WebProperties
 */
public class JiraApplicationProperties implements ApplicationProperties
{
    private final com.atlassian.jira.config.properties.ApplicationProperties applicationProperties;

    public JiraApplicationProperties(com.atlassian.jira.config.properties.ApplicationProperties applicationProperties)
    {
        this.applicationProperties = applicationProperties;
    }

    public String getBaseUrl()
    {
        return applicationProperties.getDefaultBackedString(APKeys.JIRA_BASEURL);
    }

    public String getApplicationName()
    {
        return "JIRA";
    }
}
