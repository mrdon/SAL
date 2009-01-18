package com.atlassian.sal.crowd.lifecycle;

import com.atlassian.config.lifecycle.events.ApplicationStartedEvent;
import com.atlassian.event.Event;
import com.atlassian.event.EventListener;
import com.atlassian.sal.api.lifecycle.LifecycleManager;

/**
 * Listens to ApplicationStartedEvent and notifies lifecycle manager.
 */
public class ApplicationReadyListener implements EventListener
{

	private final LifecycleManager lifecycleManager;

    public ApplicationReadyListener(final LifecycleManager lifecycleManager)
    {
        this.lifecycleManager = lifecycleManager;
    }

    @SuppressWarnings("unchecked")
	public Class[] getHandledEventClasses()
	{
		return new Class[]{ApplicationStartedEvent.class};
	}

	public void handleEvent(final Event event)
	{
		if (event instanceof ApplicationStartedEvent)
		{
			lifecycleManager.start();
		}

	}

}
