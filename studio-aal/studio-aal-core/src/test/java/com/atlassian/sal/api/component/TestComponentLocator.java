package com.atlassian.sal.api.component;

import junit.framework.TestCase;

import java.util.Collection;
import java.util.HashMap;

import com.atlassian.sal.api.component.ComponentLocator;

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
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

		@Override
		protected <T> Collection<T> getComponentsInternal(Class<T> iface)
		{
			// TODO Auto-generated method stub
			return null;
		}
    }
}
