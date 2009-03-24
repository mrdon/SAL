package com.atlassian.sal.core.lifecycle;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.commons.lang.Validate;

import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginFrameworkStartedEvent;
import com.atlassian.plugin.event.impl.DefaultPluginEventManager;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.lifecycle.LifecycleManager;

public abstract class DefaultLifecycleManager implements LifecycleManager
{
	private boolean started = false;
	private static final Logger log = Logger.getLogger(DefaultLifecycleManager.class);
	private List<LifecycleAware> listeners;

    public DefaultLifecycleManager(PluginEventManager pluginEventManager)
    {
        pluginEventManager.register(this);
    }

    /**
	 * This method will be invoked by PluginEventManager when PluginFrameworkStartedEvent event occurs.
	 * PluginEventManager uses methods called "channel" and methods with annotation "@PluginEventListener"
	 * to notify a registered listeners about events.
	 * See {@link DefaultPluginEventManager} for more details on this black magic.
	 * @param event
	 */
	@PluginEventListener
	public void onFrameworkStart(final PluginFrameworkStartedEvent event)
	{
		start();
	}

	public synchronized void start()
	{
	    if (!started && isApplicationSetUp())
	    {
	        try
	        {
	            notifyOnStart();
	        } finally
	        {
	            started = true;
	        }

	    }
	}

	/**
	 * Called by spring-osgi when when new LifecycleAware service is installed. Defined in "spring-components.xml" in "META-INF/spring/"
	 *
	 * @param service
	 * @param properties
	 */
	@SuppressWarnings("unchecked")
    public synchronized void onBind(final LifecycleAware service, final Map properties)
	{
		if (started)
			notifyLifecycleAwareOfStart(service);
	}


	protected void notifyOnStart()
	{
	    // calling listeners.iterator() will dynamically update list to get currently installed LifecycleAware components.
		for (final LifecycleAware entry : listeners)
		{
			notifyLifecycleAwareOfStart(entry);
		}
	}

	private void notifyLifecycleAwareOfStart(final LifecycleAware entry)
	{
		try
		{
			entry.onStart();
		} catch (final RuntimeException ex)
		{
			log.error("Unable to start component: " + entry.getClass().getName(), ex);
		}
	}

    public void setLifecycleAwareListeners(List<LifecycleAware> listeners)
    {
        this.listeners = listeners;
    }
}
