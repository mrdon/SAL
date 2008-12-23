package com.atlassian.sal.refimpl.lifecycle;

import java.util.List;

import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.core.lifecycle.DefaultLifecycleManager;


public class RefimplLifecycleManager extends DefaultLifecycleManager
{
	public RefimplLifecycleManager(final PluginEventManager pluginEventManager, final List<LifecycleAware> listeners)
    {
        super(pluginEventManager, listeners);
    }

    public boolean isApplicationSetUp()
	{
		return true;
	}
}
