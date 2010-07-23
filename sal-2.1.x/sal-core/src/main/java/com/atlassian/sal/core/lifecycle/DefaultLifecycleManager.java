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
    private static final Logger log = Logger.getLogger(DefaultLifecycleManager.class);

    //@GuardedBy("this")
	private boolean started = false;
	private List<LifecycleAware> listeners;
    private final PluginEventManager pluginEventManager;

    public DefaultLifecycleManager(PluginEventManager pluginEventManager)
    {
        this.pluginEventManager = pluginEventManager;
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
	 * @param service the service to notify
	 * @param properties ignored
	 */
	@SuppressWarnings("unchecked")
    public synchronized void onBind(final LifecycleAware service, final Map properties)
	{
		if (started)
        {
			notifyLifecycleAwareOfStart(service);
        }
	}

    /**
     * Unregister from the {@link PluginEventManager}.
     *
     * @since 2.3.0
     */
    public void destroy()
    {
        pluginEventManager.unregister(this);
    }

	protected void notifyOnStart()
	{
        Validate.notNull(listeners, "The list of LifecycleAware implementations hasn't been set yet and so the manager cannot start.");

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
		}
        catch (final RuntimeException ex)
		{
			log.error("Unable to start component: " + entry.getClass().getName(), ex);
		}
	}

    //@GuardedBy("spring-dm")
    public void setLifecycleAwareListeners(List<LifecycleAware> listeners)
    {
        this.listeners = listeners;
    }
}
