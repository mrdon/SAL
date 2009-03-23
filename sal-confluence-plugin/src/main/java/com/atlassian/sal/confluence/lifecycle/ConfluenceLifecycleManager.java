package com.atlassian.sal.confluence.lifecycle;

import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.sal.core.lifecycle.DefaultLifecycleManager;

public class ConfluenceLifecycleManager extends DefaultLifecycleManager
{
    public ConfluenceLifecycleManager()
    {
        super(listeners);
    }

    public boolean isApplicationSetUp()
	{
		return BootstrapUtils.getBootstrapManager().isSetupComplete();
	}
}
