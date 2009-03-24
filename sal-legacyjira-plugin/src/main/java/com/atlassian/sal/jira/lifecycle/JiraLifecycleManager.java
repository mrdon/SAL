package com.atlassian.sal.jira.lifecycle;

import com.atlassian.jira.util.JiraUtils;
import com.atlassian.sal.api.lifecycle.LifecycleManager;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.component.ComponentLocator;
import org.apache.log4j.Logger;

import java.util.Collection;

/**
 * @deprecated Extend the DefaultLifecycleManager in SAL core when JIRA becomes plugins 2
 */
@Deprecated
public class JiraLifecycleManager implements LifecycleManager
{

    private boolean started = false;
    private static final Logger log = Logger.getLogger(JiraLifecycleManager.class);

    public synchronized void start()
    {
        if (!isStarted() && isApplicationSetUp())
        {
            try
            {
                notifyOnStart();
            }
            finally
            {
                started = true;
            }

        }
    }

    protected boolean isStarted()
    {
        return started;
    }

    protected void notifyOnStart()
    {
        final Collection<LifecycleAware> listeners = ComponentLocator.getComponents(LifecycleAware.class);
        for (final LifecycleAware entry : listeners)
        {
            try
            {
                entry.onStart();
            }
            catch (final RuntimeException ex)
            {
                log.error("Unable to start component: " + entry.getClass().getName(), ex);
            }
        }
    }

    public boolean isApplicationSetUp()
    {
        return JiraUtils.isSetup();
    }

}
