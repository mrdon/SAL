package com.atlassian.sal.core.component;

import com.atlassian.sal.api.component.ComponentLocator;
import com.atlassian.sal.spi.HostContextAccessor;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.Map;

public class DefaultComponentLocator extends ComponentLocator
{
    private HostContextAccessor hostContextAccessor;
    private static final Logger log = Logger.getLogger(DefaultComponentLocator.class);

    public DefaultComponentLocator(HostContextAccessor accessor)
    {
        this.hostContextAccessor = accessor;
        ComponentLocator.setComponentLocator(this);
    }

    
    protected <T> T getComponentInternal(Class<T> iface)
    {
    	Map<String, T> beansOfType = hostContextAccessor.getComponentsOfType(iface);

    	if (beansOfType.isEmpty())
        {
            throw new RuntimeException("Could not retrieve " + iface.getName());
        } else if (beansOfType.size()>1)
        {
            // we have multiple implementations of this interface, choose one with name that looks like iface name
            String shortClassName = convertClassToName(iface);
            T implementation = (T) beansOfType.get(shortClassName);
            if (implementation == null)
            {
                log.warn("More than one instance of " + iface.getName() + " found but none of them has key " + shortClassName);
            }
            return implementation;
        }
        return (T) beansOfType.values().iterator().next();
    }

	@Override
	protected <T> T getComponentInternal(Class<T> iface, String componentId)
	{
    	Map<String, T> beansOfType = hostContextAccessor.getComponentsOfType(iface);

    	T implementation = (T) beansOfType.get(componentId);
    	if (implementation == null)
    	{
    		throw new RuntimeException("Could not retrieve " + iface.getName() + " with componentId '"+componentId+"'");
    	}
    	return implementation;
	}
    
	@Override
	protected <T> Collection<T> getComponentsInternal(Class<T> iface)
	{
		Map<String, T> beansOfType = hostContextAccessor.getComponentsOfType(iface);
		return beansOfType.values();
	}
}
