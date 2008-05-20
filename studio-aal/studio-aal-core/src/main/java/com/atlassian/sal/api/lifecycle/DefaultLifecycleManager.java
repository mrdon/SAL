package com.atlassian.sal.api.lifecycle;

import com.atlassian.sal.api.component.ComponentLocator;
import com.atlassian.sal.api.logging.Logger;
import com.atlassian.sal.api.logging.LoggerFactory;

import java.util.Collection;

public abstract class DefaultLifecycleManager implements LifecycleManager
{
    private boolean started = false;
    private final Logger log = LoggerFactory.getLogger(DefaultLifecycleManager.class);

    public synchronized void start()
    {
        if (!started && isApplicationSetUp())
        {
            try
            {
                notifyOnStart();
            } finally {
                started = true;
            }

        }
    }
    protected void notifyOnStart()
    {
        Collection<LifecycleAware> listeners = ComponentLocator.getComponents(LifecycleAware.class);
        for (LifecycleAware entry : listeners)
        {
            try
            {
                entry.onStart();
            } catch (RuntimeException ex)
            {
                log.error("Unable to start component: $1", ex, entry.getClass().getName());
            }
        }
    }
}
