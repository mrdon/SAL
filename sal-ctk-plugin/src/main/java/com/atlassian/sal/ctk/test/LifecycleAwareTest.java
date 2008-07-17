package com.atlassian.sal.ctk.test;

import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.lifecycle.LifecycleManager;
import com.atlassian.sal.ctk.CtkTest;
import com.atlassian.sal.ctk.CtkTestResults;

public class LifecycleAwareTest implements LifecycleAware, CtkTest
{
    private boolean called = false;
    private boolean calledTwice = false;
    private final LifecycleManager lifecycleManager;

    public LifecycleAwareTest(LifecycleManager lifecycleManager)
    {
        this.lifecycleManager = lifecycleManager;
    }

    public void onStart()
    {
        if (called)
            calledTwice = true;
        called = true;
    }

    public void execute(CtkTestResults results)
    {
        lifecycleManager.start();
        results.assertTrue("LifecycleAware component should be called (may need restart)", called);
        results.assertTrue("LifecycleAware component should be called only once", !calledTwice);
    }
}
