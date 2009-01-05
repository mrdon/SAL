package com.atlassian.sal.fisheye.lifecycle;



import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.atlassian.sal.core.lifecycle.DefaultLifecycleManager;
import com.cenqua.fisheye.AppConfig;
import com.cenqua.fisheye.config.AdminConfig;
import com.cenqua.fisheye.config.RootConfig;

public class FisheyeLifecycleManager extends DefaultLifecycleManager
{
    private static final Logger log = Logger.getLogger(FisheyeLifecycleManager.class);

    public boolean isApplicationSetUp()
    {
        return doInApplicationContext(new Callable<Boolean>()
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

    private <V> V doInApplicationContext(final Callable<V> callable)
    {
        final Thread currentThread = Thread.currentThread();
        final ClassLoader originalContextClassLoader = currentThread.getContextClassLoader();
        try
        {
            currentThread.setContextClassLoader(AppConfig.class.getClassLoader());
            return callable.call();
        } catch (final Exception e)
        {
            log.error(e, e);
            return null;
        } finally
        {
            currentThread.setContextClassLoader(originalContextClassLoader);
        }
    }

}
