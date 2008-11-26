package com.atlassian.sal.core.component;

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;

import com.atlassian.sal.api.component.ComponentLocator;
import com.atlassian.sal.spi.HostContextAccessor;

public class DefaultComponentLocator extends ComponentLocator
{
    private final HostContextAccessor hostContextAccessor;
    private static final Logger log = Logger.getLogger(DefaultComponentLocator.class);

    public DefaultComponentLocator(final HostContextAccessor accessor)
    {
        this.hostContextAccessor = accessor;
        ComponentLocator.setComponentLocator(this);
    }

    @Override
	protected <T> T getComponentInternal(final Class<T> iface)
    {
    	final Map<String, T> beansOfType = hostContextAccessor.getComponentsOfType(iface);

    	if (beansOfType == null || beansOfType.isEmpty())
        {
            return null;
        } else if (beansOfType.size()>1)
        {
            // we have multiple implementations of this interface, choose one with name that looks like iface name
            final String shortClassName = convertClassToName(iface);
            final T implementation = beansOfType.get(shortClassName);
            if (implementation == null)
            {
                log.warn("More than one instance of " + iface.getName() + " found but none of them has key " + shortClassName);
            }
            return implementation;
        }
        return beansOfType.values().iterator().next();
    }

	@Override
	protected <T> T getComponentInternal(final Class<T> iface, final String componentId)
	{
    	final Map<String, T> beansOfType = hostContextAccessor.getComponentsOfType(iface);

    	return beansOfType.get(componentId);
	}

	@Override
	protected <T> Collection<T> getComponentsInternal(final Class<T> iface)
	{
		final Map<String, T> beansOfType = hostContextAccessor.getComponentsOfType(iface);
		return (beansOfType != null ? beansOfType.values() : null);
	}
}
