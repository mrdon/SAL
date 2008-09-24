package com.atlassian.sal.refimpl;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.plugin.refimpl.ParameterUtils;

/**
 * Implementation of ApplicationProperties for http://localhost
 */
public class RefimplApplicationProperties implements ApplicationProperties
{
    public String getBaseUrl()
    {
        return ParameterUtils.getBaseUrl();
    }

    public String getApplicationName()
    {
        return "RefImpl";
    }
}
