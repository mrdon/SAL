package com.atlassian.sal.confluence.lifecycle;

import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.sal.core.lifecycle.DefaultLifecycleManager;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.plugin.event.PluginEventManager;

import java.util.List;

public class ConfluenceLifecycleManager extends DefaultLifecycleManager
{

    public ConfluenceLifecycleManager(PluginEventManager eventManager, List<LifecycleAware> listeners)
    {
        super(eventManager, listeners);
    }

    public boolean isApplicationSetUp()
	{
		return BootstrapUtils.getBootstrapManager().isSetupComplete();
	}
}
