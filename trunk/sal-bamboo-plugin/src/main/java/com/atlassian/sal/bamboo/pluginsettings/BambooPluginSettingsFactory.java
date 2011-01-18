package com.atlassian.sal.bamboo.pluginsettings;

import com.atlassian.bamboo.bandana.BambooBandanaContext;
import com.atlassian.bamboo.bandana.PlanAwareBandanaContext;
import com.atlassian.bamboo.build.Build;
import com.atlassian.bamboo.build.BuildManager;
import com.atlassian.bamboo.project.ProjectManager;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.core.pluginsettings.PrefixedPluginSettingsDelegate;

public class BambooPluginSettingsFactory implements PluginSettingsFactory
{
    // ------------------------------------------------------------------------------------------------------- Constants
    // ------------------------------------------------------------------------------------------------- Type Properties
    // ---------------------------------------------------------------------------------------------------- Dependencies
    private final BandanaManager bandanaManager;
    private final BuildManager buildManager;
    private final ProjectManager projectManager;

    // ---------------------------------------------------------------------------------------------------- Constructors

    public BambooPluginSettingsFactory(final BandanaManager bandanaManager, final BuildManager buildManager,
            final ProjectManager projectManager)
    {
        this.bandanaManager = bandanaManager;
        this.buildManager = buildManager;
        this.projectManager = projectManager;
    }
    // -------------------------------------------------------------------------------------------------- Public Methods

    public PluginSettings createSettingsForKey(final String key)
    {
        if (key != null)
        {
            final Build build = buildManager.getBuildByKey(key);
            if (build != null)
            {
                final BambooBandanaContext context = new PlanAwareBandanaContext(build.getId());
                return new BambooPluginSettings(bandanaManager, context);
            }
            else
            {
                // See if a project with that key exists
                if (projectManager.getProjectByKey(key) != null)
                {
                    return new PrefixedPluginSettingsDelegate(new StringBuilder("__").append(key).append(
                    ".").toString(), createGlobalSettings());
                }
                else
                {
                    throw new IllegalArgumentException(
                            "Could no create Plugin Settings no build with key \"" + key + "\" exists.");
                }
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
