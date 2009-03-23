package com.atlassian.sal.crowd.lifecycle;

import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.sal.core.lifecycle.DefaultLifecycleManager;

public class CrowdLifecycleManager extends DefaultLifecycleManager
{
    public CrowdLifecycleManager()
    {
        super(listeners);
    }

    public boolean isApplicationSetUp()
	{
		return BootstrapUtils.getBootstrapManager().isSetupComplete();
	}
}
