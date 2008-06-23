package com.atlassian.sal.api.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mock implementation of the component locator for testing
 */
public class MockComponentLocator extends ComponentLocator
{
    Map<Class,Object> components = new HashMap();

    public MockComponentLocator(Object... objects)
    {
        if (objects != null && objects.length > 0)
        {
            for (Object o : objects)
            {
                add(o);
            }
        }
    }

    public MockComponentLocator add(Class cls, Object obj)
    {
        components.put(cls, obj);
        return this;
    }

    public MockComponentLocator add(Object obj)
    {
        List<Class> interfaces = new ArrayList<Class>();
        Class cls = obj.getClass();
        while (cls != null)
        {
            interfaces.addAll(Arrays.asList(cls.getInterfaces()));
            cls = cls.getSuperclass();
        }
        components.put(interfaces.get(0), obj);
        return this;
    }

    protected <T> T getComponentInternal(Class<T> iface)
    {
        return (T) components.get(iface);
    }

	public static MockComponentLocator create(Object... objects)
	{
		final MockComponentLocator mockComponentLocator = new MockComponentLocator(objects);
		ComponentLocator.setComponentLocator(mockComponentLocator);
		return mockComponentLocator;
	}

	@Override
	protected <T> Collection<T> getComponentsInternal(Class<T> iface)
	{
		return null;
	}
	
	@Override
	protected <T> T getComponentInternal(Class<T> iface, String componentId)
	{
		return getComponentInternal(iface);
	}


}
