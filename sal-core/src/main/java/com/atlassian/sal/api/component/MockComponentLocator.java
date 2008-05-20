package com.atlassian.sal.api.component;

import java.util.*;

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

	@Override
	protected <T> Collection<T> getComponentsInternal(Class<T> iface)
	{
		return null;
	}
	
	public static MockComponentLocator create()
	{
		final MockComponentLocator mockComponentLocator = new MockComponentLocator();
		ComponentLocator.setComponentLocator(mockComponentLocator);
		return mockComponentLocator;
	}


}
