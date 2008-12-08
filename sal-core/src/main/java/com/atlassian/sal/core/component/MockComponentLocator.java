package com.atlassian.sal.core.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.sal.api.component.ComponentLocator;

/**
 * Mock implementation of the component locator for testing
 */
public class MockComponentLocator extends ComponentLocator
{
    Map<Class<?>,Object> components = new HashMap<Class<?>, Object>();

    public MockComponentLocator(final Object... objects)
    {
        if (objects != null && objects.length > 0)
        {
            for (final Object o : objects)
            {
                add(o);
            }
        }
    }

    public MockComponentLocator add(final Class<?> cls, final Object obj)
    {
        components.put(cls, obj);
        return this;
    }

    public MockComponentLocator add(final Object obj)
    {
        final List<Class> interfaces = new ArrayList<Class>();
        Class<?> cls = obj.getClass();
        while (cls != null)
        {
            // get all interfaces of this class
            interfaces.addAll(Arrays.asList(cls.getInterfaces()));
            // get a superclass
            interfaces.add(cls);
            cls = cls.getSuperclass();
        }
        // register this component with all its interfaces and supers
        for (final Class<?> iface : interfaces)
        {
            components.put(iface, obj);
        }
        return this;
    }

    @Override
    protected <T> T getComponentInternal(final Class<T> iface)
    {
        return (T) components.get(iface);
    }

	public static MockComponentLocator create(final Object... objects)
	{
		final MockComponentLocator mockComponentLocator = new MockComponentLocator(objects);
		ComponentLocator.setComponentLocator(mockComponentLocator);
		return mockComponentLocator;
	}

	@Override
	protected <T> Collection<T> getComponentsInternal(final Class<T> iface)
	{
		return null;
	}

	@Override
	protected <T> T getComponentInternal(final Class<T> iface, final String componentId)
	{
		return getComponentInternal(iface);
	}


}
