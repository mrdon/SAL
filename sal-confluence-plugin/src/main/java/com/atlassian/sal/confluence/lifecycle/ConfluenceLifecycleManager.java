package com.atlassian.sal.confluence.lifecycle;

import java.util.List;

import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.core.lifecycle.DefaultLifecycleManager;

public class ConfluenceLifecycleManager extends DefaultLifecycleManager
{
    public ConfluenceLifecycleManager(final PluginEventManager pluginEventManager, final List<LifecycleAware> listeners)
    {
        super(pluginEventManager, listeners);
    }

    public boolean isApplicationSetUp()
	{
		return BootstrapUtils.getBootstrapManager().isSetupComplete();
	}
}
