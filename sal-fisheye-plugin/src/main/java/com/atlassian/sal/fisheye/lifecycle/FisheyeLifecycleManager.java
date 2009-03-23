package com.atlassian.sal.fisheye.lifecycle;

import com.atlassian.sal.core.lifecycle.DefaultLifecycleManager;
import com.atlassian.sal.fisheye.appconfig.FisheyeAccessor;

public class FisheyeLifecycleManager extends DefaultLifecycleManager
{

    private final FisheyeAccessor fisheyeAccessor;

    public FisheyeLifecycleManager(final FisheyeAccessor fisheyeAccessor)
    {
        super(listeners);
        this.fisheyeAccessor = fisheyeAccessor;
    }

    public boolean isApplicationSetUp()
	{
        return fisheyeAccessor.isApplicationSetUp();
	}

}
