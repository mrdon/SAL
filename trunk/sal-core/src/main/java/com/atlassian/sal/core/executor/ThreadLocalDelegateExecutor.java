package com.atlassian.sal.core.executor;

import java.util.concurrent.Executor;

/**
 * Executor that wraps executing runnables in a wrapper that transfers the threadlocal context
 */
class ThreadLocalDelegateExecutor implements Executor
{
    private final Executor delegate;
    protected final ThreadLocalContextManager manager;

    ThreadLocalDelegateExecutor(ThreadLocalContextManager manager, Executor delegate)
    {
        this.delegate = delegate;
        this.manager = manager;
    }

    public void execute(Runnable runnable)
    {
        Runnable wrapper = new ThreadLocalDelegateRunnable(manager, runnable);

        delegate.execute(wrapper);
    }
}
