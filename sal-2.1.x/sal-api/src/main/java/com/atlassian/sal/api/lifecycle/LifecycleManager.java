package com.atlassian.sal.api.lifecycle;

/**
 * Interface to be used to trigger lifecycle events
 *
 * @since 2.0
 */
public interface LifecycleManager
{
    /**
     * Triggers a start lifecycle event once and only once.  This method can be called multiple times but will only fire
     * an onStart event once and only when application is already set up.
     */
    void start();

    /**
     * @return true if application is set up and ready to run
     */
    boolean isApplicationSetUp();
}
