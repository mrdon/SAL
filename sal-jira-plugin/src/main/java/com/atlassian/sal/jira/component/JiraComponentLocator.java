package com.atlassian.sal.jira.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;

import org.apache.log4j.Logger;

import com.atlassian.jira.ComponentManager;
import com.atlassian.sal.api.component.ComponentLocator;

public class JiraComponentLocator extends ComponentLocator
{
    private static final Logger log = Logger.getLogger(JiraComponentLocator.class);
    public JiraComponentLocator()
    {
        ComponentLocator.setComponentLocator(this);
    }

    @Override
    @SuppressWarnings("unchecked")
	protected <T> T getComponentInternal(final Class<T> iface)
    {
        return (T) ComponentManager.getComponentInstanceOfType(iface);
    }

	@Override
	protected <T> Collection<T> getComponentsInternal(final Class<T> iface)
	{
		final Collection<T> implementations = new ArrayList<T>();
		final PicoContainer picoContainer = ComponentManager.getInstance().getContainer();
		final List componentAdaptersOfType = picoContainer.getComponentAdaptersOfType(iface);
		for (final Iterator iterator = componentAdaptersOfType.iterator(); iterator.hasNext();)
		{
			final ComponentAdapter componentAdapter = (ComponentAdapter) iterator.next();
			implementations.add((T) componentAdapter.getComponentInstance());
		}
		return implementations;
	}

	@Override
	protected <T> T getComponentInternal(final Class<T> iface, final String componentKey)
	{
	    final Class<?> key;
	    try
        {
            key = Class.forName(componentKey);
        } catch (final ClassNotFoundException e)
        {
            log.warn(e.getMessage(),e);
            return null;
        }
		final PicoContainer picoContainer = ComponentManager.getInstance().getContainer();
		final List componentAdaptersOfType = picoContainer.getComponentAdaptersOfType(iface);
		for (final Iterator iterator = componentAdaptersOfType.iterator(); iterator.hasNext();)
		{
			final ComponentAdapter componentAdapter = (ComponentAdapter) iterator.next();
			if (key.equals(componentAdapter.getComponentKey()))
					return (T) componentAdapter.getComponentInstance();
		}
		return null;
	}
}
