package com.atlassian.sal.refimpl;

import com.atlassian.sal.api.ApplicationProperties;

/**
 * Implementation of ApplicationProperties for http://localhost
 */
public class RefimplApplicationProperties implements ApplicationProperties
{
    public String getBaseUrl()
    {
        return System.getProperty("baseurl", "http://localhost:8080/atlassian-plugins-refimpl");
    }

    public String getApplicationName()
    {
        return "RefImpl";
    }
}
