package com.atlassian.sal.refimpl.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.atlassian.sal.api.component.ComponentLocator;

public class RefimplComponentLocator extends ComponentLocator
{
    public RefimplComponentLocator()
    {
        ComponentLocator.setComponentLocator(this);
    }

    @SuppressWarnings("unchecked")
	protected <T> T getComponentInternal(Class<T> iface)
    {
        throw new UnsupportedOperationException("Not supported");
    }

	@Override
	protected <T> Collection<T> getComponentsInternal(Class<T> iface)
	{
		throw new UnsupportedOperationException("Not supported");
	}

	@Override
	protected <T> T getComponentInternal(Class<T> iface, String componentKey)
	{
		throw new UnsupportedOperationException("Not supported");
	}
}
