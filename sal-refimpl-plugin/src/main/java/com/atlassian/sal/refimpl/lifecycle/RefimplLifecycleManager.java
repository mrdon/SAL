package com.atlassian.sal.refimpl.lifecycle;

import com.atlassian.sal.core.lifecycle.DefaultLifecycleManager;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.plugin.event.PluginEventManager;

import java.util.List;


public class RefimplLifecycleManager extends DefaultLifecycleManager
{
    public RefimplLifecycleManager(PluginEventManager eventManager, List<LifecycleAware> listeners)
    {
        super(eventManager, listeners);
    }

    public boolean isApplicationSetUp()
	{
		return true;
	}
	
}