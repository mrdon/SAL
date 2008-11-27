package com.atlassian.sal.core.lifecycle;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.atlassian.sal.api.lifecycle.LifecycleManager;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginFrameworkStartedEvent;

public abstract class DefaultLifecycleManager implements LifecycleManager
{
    private boolean started = false;
    private static final Logger log = Logger.getLogger(DefaultLifecycleManager.class);
    private final List<LifecycleAware> listeners;

    public DefaultLifecycleManager(PluginEventManager eventManager, List<LifecycleAware> listeners)
    {
        if (eventManager != null)
            eventManager.register(this);
        this.listeners = listeners;
    }

    public void channel(PluginFrameworkStartedEvent event)
    {
        start();
    }

    public void onUnbind(LifecycleAware service, Map properties)
    {
        // do nothing
    }

    public synchronized void onBind(LifecycleAware service, Map properties)
    {
        if (started)
            notifyLifecycleAwareOfStart(service);
    }

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
        for (LifecycleAware entry : listeners)
        {
            notifyLifecycleAwareOfStart(entry);
        }
    }

    private void notifyLifecycleAwareOfStart(LifecycleAware entry)
    {
        try
            {
                entry.onStart();
            } catch (RuntimeException ex)
        {
            log.error("Unable to start component: "+ entry.getClass().getName(), ex);
        }
    }
}
