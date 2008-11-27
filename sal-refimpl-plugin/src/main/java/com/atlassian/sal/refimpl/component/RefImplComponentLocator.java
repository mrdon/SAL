package com.atlassian.sal.refimpl.component;

import java.util.Collection;

import com.atlassian.sal.api.component.ComponentLocator;

public class RefImplComponentLocator extends ComponentLocator
{
    public RefImplComponentLocator()
    {
        setComponentLocator(this);
    }

    protected <T> T getComponentInternal(Class<T> iface)
    {
        throw new UnsupportedOperationException("Use injection, not this service lookup.  What do you think this is, the 90's?");
    }

    protected <T> T getComponentInternal(Class<T> iface, String componentKey)
    {
        throw new UnsupportedOperationException("Use injection, not this service lookup.  What do you think this is, the 90's?");
    }

    protected <T> Collection<T> getComponentsInternal(Class<T> iface)
    {
        throw new UnsupportedOperationException("Use injection, not this service lookup.  What do you think this is, the 90's?");
    }
}
