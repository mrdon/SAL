package com.atlassian.sal.bamboo;

import org.apache.log4j.Logger;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.bamboo.util.BuildUtils;
import com.atlassian.bamboo.configuration.AdministrationConfigurationManager;
import com.atlassian.config.HomeLocator;

import java.util.Date;
import java.io.File;

public class BambooApplicationProperties implements ApplicationProperties
{
    private static final Logger log = Logger.getLogger(BambooApplicationProperties.class);
    // ------------------------------------------------------------------------------------------------------- Constants
    // ------------------------------------------------------------------------------------------------- Type Properties
    // ---------------------------------------------------------------------------------------------------- Dependencies
    private final AdministrationConfigurationManager administrationConfigurationManager;
    private final HomeLocator homeLocator;

    // ---------------------------------------------------------------------------------------------------- Constructors

    public BambooApplicationProperties(AdministrationConfigurationManager administrationConfigurationManager, HomeLocator homeLocator)
    {
        this.administrationConfigurationManager = administrationConfigurationManager;
        this.homeLocator = homeLocator;
    }
    // ----------------------------------------------------------------------------------------------- Interface Methods
    // -------------------------------------------------------------------------------------------------- Action Methods
    // -------------------------------------------------------------------------------------------------- Public Methods

    public String getBaseUrl()
    {
        return administrationConfigurationManager.getAdministrationConfiguration().getBaseUrl();
    }

    public String getVersion()
    {
        return BuildUtils.getCurrentVersion();
    }

    public Date getBuildDate()
    {
        return BuildUtils.getCurrentBuildDate();
    }

    public String getBuildNumber()
    {
        return BuildUtils.getCurrentBuildNumber();
    }

    public String getDisplayName()
    {
        return "Bamboo";
    }

    public File getHomeDirectory()
    {
        return new File(homeLocator.getHomePath());
    }

    // ------------------------------------------------------------------------------------------------- Helper Methods
    // -------------------------------------------------------------------------------------- Basic Accessors / Mutators
}
