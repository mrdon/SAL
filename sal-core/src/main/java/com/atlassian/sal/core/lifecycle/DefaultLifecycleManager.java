package com.atlassian.sal.core.lifecycle;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

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

	/**
	 * This method will be invoked by PluginEventManager when PluginFrameworkStartedEvent event occurs.
	 * PluginEventManager uses methods called "channel" and methods with annotation "@PluginEventListener"
	 * to notify a registered listeners about events.
	 * See {@link DefaultPluginEventManager} for more details on this black magic.
	 * @param event
	 */
	public void channel(final PluginFrameworkStartedEvent event)
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

	public void setListeners(final List<LifecycleAware> listeners)
	{
		this.listeners = listeners;
	}

	public void setPluginEventManager(final PluginEventManager pluginEventManager)
    {
        pluginEventManager.register(this);
    }


}
