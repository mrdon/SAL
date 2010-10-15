package com.atlassian.sal.core.component;

import java.util.Collection;
import java.util.HashMap;

import junit.framework.TestCase;
import com.atlassian.sal.api.component.ComponentLocator;

public class TestComponentLocator extends TestCase
{
    public void testConvertClassToName()
    {
        MockComponentLocator loc = new MockComponentLocator();
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

        @Override
        protected String convertClassToName(Class cls)
        {
            return super.convertClassToName(cls);
        }
    }
}
