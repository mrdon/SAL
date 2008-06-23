package com.atlassian.sal.api.component;

import java.util.Collection;
import java.util.HashMap;

import junit.framework.TestCase;

public class TestComponentLocator extends TestCase
{
    public void testConvertClassToName()
    {
        ComponentLocator loc = new MockComponentLocator();
        assertEquals("string", loc.convertClassToName(String.class));
        assertEquals("hashMap", loc.convertClassToName(HashMap.class));
    }

    private static class MockComponentLocator extends ComponentLocator
    {

        protected <T> T getComponentInternal(Class<T> iface)
        {
            return null;
        }

		@Override
		protected <T> Collection<T> getComponentsInternal(Class<T> iface)
		{
			return null;
		}

		@Override
		protected <T> T getComponentInternal(Class<T> iface, String componentId)
		{
			return null;
		}
    }
}
