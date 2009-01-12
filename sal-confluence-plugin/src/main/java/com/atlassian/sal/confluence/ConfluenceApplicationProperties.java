package com.atlassian.sal.confluence;

import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.sal.api.ApplicationProperties;

import java.util.Date;

/**
 * Confluence implementation of the AAL Web Properties.
 */
public class ConfluenceApplicationProperties implements ApplicationProperties
{
    private SettingsManager settingsManager;

    public String getBaseUrl()
    {
        return settingsManager.getGlobalSettings().getBaseUrl();
    }

    public String getApplicationName()
    {
        return "Confluence";
    }

    public String getVersion()
    {
        return GeneralUtil.getVersionNumber();
    }

    public Date getBuildDate()
    {
        return GeneralUtil.getBuildDate();
    }

    public String getBuildNumber()
    {
        return GeneralUtil.getBuildNumber();
    }

    public void setSettingsManager(SettingsManager settingsManager)
    {
        this.settingsManager = settingsManager;
    }
}

