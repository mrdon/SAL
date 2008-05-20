package com.atlassian.sal.confluence.lifecycle;

import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.sal.api.lifecycle.DefaultLifecycleManager;


public class ConfluenceLifecycleManager extends DefaultLifecycleManager
{

	public boolean isApplicationSetUp()
	{
		return BootstrapUtils.getBootstrapManager().isSetupComplete();
	}
}
