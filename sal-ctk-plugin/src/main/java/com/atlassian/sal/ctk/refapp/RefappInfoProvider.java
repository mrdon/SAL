package com.atlassian.sal.ctk.refapp;

import com.atlassian.sal.ctk.AppSpecificInfoProvider;

/**
 * Info provider for refapp which has "admin"/"admin" user pass as default.
 */
public class RefappInfoProvider implements AppSpecificInfoProvider
{
    public String getAdminUsername()
    {
        return "admin";
    }

    public String getAdminPassword()
    {
        return "admin";
    }
}
