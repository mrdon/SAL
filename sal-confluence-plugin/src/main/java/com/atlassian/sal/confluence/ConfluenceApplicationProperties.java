package com.atlassian.sal.confluence;

import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.sal.api.ApplicationProperties;

import java.util.Date;
import java.io.File;

/**
 * Confluence implementation of the AAL Web Properties.
 */
public class ConfluenceApplicationProperties implements ApplicationProperties
{
    private SettingsManager settingsManager;
    private BootstrapManager bootstrapManager;

    public String getBaseUrl()
    {
        return settingsManager.getGlobalSettings().getBaseUrl();
    }

    public String getDisplayName()
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

    public File getHomeDirectory()
    {
        String path = bootstrapManager.getConfluenceHome();
        if (path != null)
        {
            return new File(path);
        }
        else
        {
            return null;
        }


    }

    public void setSettingsManager(SettingsManager settingsManager)
    {
        this.settingsManager = settingsManager;
    }

    public void setBootstrapManager(BootstrapManager bootstrapManager)
    {
        this.bootstrapManager = bootstrapManager;
    }
}

