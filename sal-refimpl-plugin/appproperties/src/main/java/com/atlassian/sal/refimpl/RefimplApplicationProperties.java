package com.atlassian.sal.refimpl;

import com.atlassian.sal.api.ApplicationProperties;

import java.util.Date;

/**
 * Implementation of ApplicationProperties for http://localhost
 */
public class RefimplApplicationProperties implements ApplicationProperties
{
    private static final Date THEN = new Date();

    public String getBaseUrl()
    {
        return System.getProperty("baseurl", "http://localhost:8080/atlassian-plugins-refimpl");
    }

    public String getApplicationName()
    {
        return "RefImpl";
    }

    public String getVersion()
    {
        return "1.0";
    }

    public Date getBuildDate()
    {
        return THEN;
    }

    public String getBuildNumber()
    {
        return "123";
    }
}