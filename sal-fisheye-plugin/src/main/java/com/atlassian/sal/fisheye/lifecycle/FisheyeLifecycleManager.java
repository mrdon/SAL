package com.atlassian.sal.fisheye.lifecycle;

import com.atlassian.sal.core.lifecycle.DefaultLifecycleManager;
import com.atlassian.sal.fisheye.appconfig.FisheyeAccessor;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.plugin.event.PluginEventManager;

import java.util.List;

public class FisheyeLifecycleManager extends DefaultLifecycleManager
{

    private final FisheyeAccessor fisheyeAccessor;

    public FisheyeLifecycleManager(final FisheyeAccessor fisheyeAccessor, PluginEventManager pluginEventManager,
                                   List<LifecycleAware> listeners)
    {
        super(pluginEventManager, listeners);
        this.fisheyeAccessor = fisheyeAccessor;
    }

    public boolean isApplicationSetUp()
	{
        return fisheyeAccessor.isApplicationSetUp();
	}

}
