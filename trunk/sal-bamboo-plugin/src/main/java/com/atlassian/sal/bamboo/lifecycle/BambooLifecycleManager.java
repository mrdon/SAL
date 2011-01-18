package com.atlassian.sal.bamboo.lifecycle;

import org.apache.log4j.Logger;
import com.atlassian.sal.core.lifecycle.DefaultLifecycleManager;
import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.plugin.event.PluginEventManager;

public class BambooLifecycleManager extends DefaultLifecycleManager
{
    private static final Logger log = Logger.getLogger(BambooLifecycleManager.class);
    // ------------------------------------------------------------------------------------------------------- Constants
    // ------------------------------------------------------------------------------------------------- Type Properties
    // ---------------------------------------------------------------------------------------------------- Dependencies
    // ---------------------------------------------------------------------------------------------------- Constructors
    public BambooLifecycleManager(PluginEventManager pluginEventManager)
    {
        super(pluginEventManager);
    }
    // -------------------------------------- --------------------------------------------------------- Interface Methods
    // -------------------------------------------------------------------------------------------------- Action Methods
    // -------------------------------------------------------------------------------------------------- Public Methods

    public boolean isApplicationSetUp()
    {
        return BootstrapUtils.getBootstrapManager().isSetupComplete();
    }
    // ------------------------------------------------------------------------------------------------- Helper Methods
    // -------------------------------------------------------------------------------------- Basic Accessors / Mutators
}
