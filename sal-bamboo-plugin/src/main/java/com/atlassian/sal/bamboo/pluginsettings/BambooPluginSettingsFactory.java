package com.atlassian.sal.bamboo.pluginsettings;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.bamboo.bandana.BambooBandanaContext;
import com.atlassian.bamboo.bandana.PlanAwareBandanaContext;
import com.atlassian.bamboo.build.BuildManager;
import com.atlassian.bamboo.build.Build;
import org.apache.log4j.Logger;

public class BambooPluginSettingsFactory implements PluginSettingsFactory
{
    private static final Logger log = Logger.getLogger(BambooPluginSettingsFactory.class);
    // ------------------------------------------------------------------------------------------------------- Constants
    // ------------------------------------------------------------------------------------------------- Type Properties
    // ---------------------------------------------------------------------------------------------------- Dependencies
    private final BandanaManager bandanaManager;
    private final BuildManager buildManager;

    // ---------------------------------------------------------------------------------------------------- Constructors

    public BambooPluginSettingsFactory(BandanaManager bandanaManager, BuildManager buildManager)
    {
        this.bandanaManager = bandanaManager;
        this.buildManager = buildManager;
    }
    // -------------------------------------------------------------------------------------------------- Public Methods

    public PluginSettings createSettingsForKey(String key)
    {
        if (key != null)
        {
            Build build = buildManager.getBuildByKey(key);
            if (build != null)
            {
                BambooBandanaContext context = new PlanAwareBandanaContext(build.getId());
                return new BambooPluginSettings(bandanaManager, context);
            }
            else
            {
                throw new IllegalArgumentException("Could no create Plugin Settings no build with key \"" + key + "\" exists.");
            }
        }
        else
        {
            return new BambooPluginSettings(bandanaManager, PlanAwareBandanaContext.GLOBAL_CONTEXT);
        }
    }

    public PluginSettings createGlobalSettings()
    {
        return createSettingsForKey(null);
    }

    // ------------------------------------------------------------------------------------------------- Helper Methods
    // -------------------------------------------------------------------------------------- Basic Accessors / Mutators
}
