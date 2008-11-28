package com.atlassian.sal.fisheye.lifecycle;

import com.atlassian.sal.core.lifecycle.DefaultLifecycleManager;
import com.cenqua.fisheye.AppConfig;
import com.cenqua.fisheye.config.AdminConfig;
import com.cenqua.fisheye.config.RootConfig;

public class FisheyeLifecycleManager extends DefaultLifecycleManager
{

    public boolean isApplicationSetUp()
	{
		// this code is copied from TotalityFilter.requresSetup() method
		final RootConfig rootConfig = AppConfig.getsConfig();
        final AdminConfig acfg = rootConfig.getAdminConfig();
        final boolean requiresSetup = !acfg.haveDoneInitialSetup() || (rootConfig.getLicense() == null) || rootConfig.getLicense().isTerminated();
        return !requiresSetup;
	}

}
