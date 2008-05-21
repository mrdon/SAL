package com.atlassian.sal.api.component;

import java.util.Collection;

/**
 * Unified interface to access components via their interface.  Calling {@link #getComponent(Class)} will work the
 * same in any application, regardless of underlying dependency injection system used.
 */
public abstract class ComponentLocator
{
    private static ComponentLocator componentLocator;

    /**
     * Sets the component locator to use.  Should only be called once.
     * @param loc The implementation to use
     */
    public static void setComponentLocator(ComponentLocator loc)
    {
        ComponentLocator.componentLocator = loc;
    }

    /**
     * Gets a component by its interface.  Applications that don't support interface-based components will need to
     * covert the interface name into a String
     * @param iface The interface to find an implementation for
     * @return The implementation
     */
    public static <T> T getComponent(Class<T> iface)
    {
        return componentLocator.getComponentInternal(iface);
    }

    /**
     * Gets the requested component, to be overridden for each application
     * @param iface The interface to lookup
     * @return The implementation
     */
    protected abstract <T> T getComponentInternal(Class<T> iface);

    /**
     * Gets a components by interface.  Applications that don't support interface-based components will need to
     * covert the interface name into a String
     * @param iface The interface to find an implementation for
     * @return The implementation
     */
    public static <T> Collection<T> getComponents(Class<T> iface)
    {
    	return componentLocator.getComponentsInternal(iface);
    }

    protected abstract <T> Collection<T> getComponentsInternal(Class<T> iface);

    /**
     * Converts the interface name into a String key
     * @param iface The interface to convert
     * @return The String key to use to find the implementation
     */
    protected String convertClassToName(Class iface)
    {
        return Character.toLowerCase(iface.getSimpleName().charAt(0)) + iface.getSimpleName().substring(1);
    }

}
