package com.atlassian.sal.jira.component;

import com.atlassian.jira.ComponentManager;
import com.atlassian.sal.api.component.ComponentLocator;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class JiraComponentLocator extends ComponentLocator
{
    public JiraComponentLocator()
    {
        ComponentLocator.setComponentLocator(this);
    }

    @SuppressWarnings("unchecked")
	protected <T> T getComponentInternal(Class<T> iface)
    {
        return (T) ComponentManager.getComponentInstanceOfType(iface);
    }

	@Override
	protected <T> Collection<T> getComponentsInternal(Class<T> iface)
	{
		Collection<T> implementations = new ArrayList<T>();
		PicoContainer picoContainer = ComponentManager.getInstance().getContainer();
		List componentAdaptersOfType = picoContainer.getComponentAdaptersOfType(iface);
		for (Iterator iterator = componentAdaptersOfType.iterator(); iterator.hasNext();)
		{
			ComponentAdapter componentAdapter = (ComponentAdapter) iterator.next();
			implementations.add((T) componentAdapter.getComponentInstance());
		}
		return implementations;
	}
}
