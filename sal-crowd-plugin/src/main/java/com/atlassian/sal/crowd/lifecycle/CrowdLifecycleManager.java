package com.atlassian.sal.crowd.lifecycle;

import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.sal.core.lifecycle.DefaultLifecycleManager;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.plugin.event.PluginEventManager;

import java.util.List;

public class CrowdLifecycleManager extends DefaultLifecycleManager
{
    public CrowdLifecycleManager(PluginEventManager pluginEventManager, List<LifecycleAware> listeners)
    {
        super(pluginEventManager, listeners);
    }

    public boolean isApplicationSetUp()
	{
		return BootstrapUtils.getBootstrapManager().isSetupComplete();
	}
}
