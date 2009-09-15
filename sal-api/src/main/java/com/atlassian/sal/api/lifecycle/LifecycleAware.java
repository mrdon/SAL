package com.atlassian.sal.api.lifecycle;

/**
 * Marks a class that wants to execute code on certain application-level lifecycle stages
 * This only works on public components. This means the class must be listed as a component
 * in the atlassian-plugins.xml and must be marked as public="true" and include an
 * interface="com.atlassian.sal.api.lifecycle.LifecycleAware" section
 *
 * @since 2.0
 */
public interface LifecycleAware
{
    /**
     * Called when the application has started or has been restored from backup
     */
    void onStart();
}
