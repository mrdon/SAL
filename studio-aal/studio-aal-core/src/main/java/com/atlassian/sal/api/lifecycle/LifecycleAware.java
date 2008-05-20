package com.atlassian.sal.api.lifecycle;

/**
 * Marks a class that wants to execute code on certain application-level lifecycle stages
 */
public interface LifecycleAware
{
    /**
     * Called when the application has started or has been restored from backup
     */
    void onStart();
}
