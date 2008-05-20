package com.atlassian.sal.confluence;

import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.sal.api.ApplicationProperties;

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

    public void setSettingsManager(SettingsManager settingsManager)
    {
        this.settingsManager = settingsManager;
    }
}

