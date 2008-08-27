package com.atlassian.sal.refimpl.component;

import com.atlassian.sal.api.component.ComponentLocator;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class RefImplComponentLocator extends ComponentLocator
{
    private Map<Class,Object> container;

    public RefImplComponentLocator(Map<Class,Object> map) {
        setComponentLocator(this);
        this.container = map;
    }
    protected <T> T getComponentInternal(Class<T> iface)
    {
        return (T) container.get(iface);
    }


    protected <T> T getComponentInternal(Class<T> iface, String componentKey)
    {
        return (T) container.get(iface);
    }

    protected <T> Collection<T> getComponentsInternal(Class<T> iface)
    {
        return (Collection<T>) Collections.singletonList(container.get(iface));
    }
}
