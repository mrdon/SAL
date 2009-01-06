package com.atlassian.sal.fisheye.lifecycle;



import java.util.concurrent.Callable;

import com.atlassian.sal.core.lifecycle.DefaultLifecycleManager;
import com.atlassian.sal.fisheye.Plugins2Hacks;
import com.cenqua.fisheye.AppConfig;
import com.cenqua.fisheye.config.AdminConfig;
import com.cenqua.fisheye.config.RootConfig;

public class FisheyeLifecycleManager extends DefaultLifecycleManager
{
    public boolean isApplicationSetUp()
    {
        return Plugins2Hacks.doInApplicationContext(new Callable<Boolean>()
        {
            public Boolean call() throws Exception
            {
                return _isApplicationSetUp();
            }
        });
    }

    public boolean _isApplicationSetUp()
	{
		// this code is copied from TotalityFilter.requresSetup() method
		final RootConfig rootConfig = AppConfig.getsConfig();
        final AdminConfig acfg = rootConfig.getAdminConfig();
        final boolean requiresSetup = !acfg.haveDoneInitialSetup() || (rootConfig.getLicense() == null) || rootConfig.getLicense().isTerminated();
        return !requiresSetup;
	}

}
