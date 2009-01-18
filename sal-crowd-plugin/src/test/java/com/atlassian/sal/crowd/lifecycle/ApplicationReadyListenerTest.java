package com.atlassian.sal.crowd.lifecycle;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import junit.framework.TestCase;

import com.atlassian.config.lifecycle.events.ApplicationStartedEvent;
import com.atlassian.sal.api.lifecycle.LifecycleManager;

public class ApplicationReadyListenerTest extends TestCase
{

	@SuppressWarnings("unchecked")
	public void testGetHandledEventClasses()
	{
		final ApplicationReadyListener applicationReadyListener = new ApplicationReadyListener(null);
		final Class[] handledEventClasses = applicationReadyListener.getHandledEventClasses();
		assertEquals(1, handledEventClasses.length);
		assertEquals(ApplicationStartedEvent.class, handledEventClasses[0]);
	}

	public void testHandleEvent()
	{
		// create mocks
		final LifecycleManager lifeCycleManager = mock(LifecycleManager.class);
		final ApplicationReadyListener applicationReadyListener = new ApplicationReadyListener(lifeCycleManager);

		// Call handleEvent with null event
		applicationReadyListener.handleEvent(null);
		// verify
		verify(lifeCycleManager, never()).start();

		// Try again with real event
		applicationReadyListener.handleEvent(new ApplicationStartedEvent(this));
		// verify
		verify(lifeCycleManager).start();

	}

}
