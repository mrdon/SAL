package com.atlassian.sal.core.executor;

import com.atlassian.sal.core.executor.ThreadLocalContextManager;

import java.util.concurrent.Callable;

/**
 * A wrapping callable that copies the thread local state into the calling code
 */
class ThreadLocalDelegateCallable<T> implements Callable<T>
{
    private final Callable<T> delegate;
    private final ThreadLocalContextManager manager;
    private final Object context;

    ThreadLocalDelegateCallable(ThreadLocalContextManager manager, Callable<T> delegate)
    {
        this.delegate = delegate;
        this.manager = manager;
        context = manager.getThreadLocalContext();
    }

    public T call() throws Exception
    {
        manager.setThreadLocalContext(context);
        try
        {
            return delegate.call();
        }
        finally
        {
            manager.clearThreadLocalContext();
        }
    }
}
