package com.atlassian.sal.refimpl;

import com.atlassian.sal.api.ApplicationProperties;

/**
 * Implementation of ApplicationProperties for http://localhost
 */
public class RefimplApplicationProperties implements ApplicationProperties
{
    public String getBaseUrl()
    {
        return "http://localhost";
    }

    public String getApplicationName()
    {
        return "RefImpl";
    }
}
