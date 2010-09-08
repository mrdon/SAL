package com.atlassian.sal.ctk;

import com.atlassian.sal.api.lifecycle.LifecycleAware;

import java.util.concurrent.atomic.AtomicInteger;

public class LifeCycleAwareStateHolder implements LifecycleAware
{
    private AtomicInteger calledCount = new AtomicInteger(0);

    public void onStart()
    {
        // just to avoid false positive if it gets called more than once concurrently.
        calledCount.incrementAndGet();
    }

    public int getCalledCount()
    {
        return calledCount.get();
    }
}
