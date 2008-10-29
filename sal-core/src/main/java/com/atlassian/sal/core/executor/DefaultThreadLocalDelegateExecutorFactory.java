package com.atlassian.sal.core.executor;

import com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.Callable;

/**
 * Creates delegating executable classes that copy thread local state
 */
public class DefaultThreadLocalDelegateExecutorFactory implements ThreadLocalDelegateExecutorFactory
{
    private final ThreadLocalContextManager manager;

    protected DefaultThreadLocalDelegateExecutorFactory(ThreadLocalContextManager manager)
    {
        this.manager = manager;
    }

    public Executor createExecutor(Executor delegate)
    {
        return new ThreadLocalDelegateExecutor(manager, delegate);
    }

    public Runnable createRunnable(Runnable delegate)
    {
        return new ThreadLocalDelegateRunnable(manager, delegate);
    }

    public <T> Callable<T> createCallable(Callable<T> delegate)
    {
        return new ThreadLocalDelegateCallable<T>(manager, delegate);
    }
}
