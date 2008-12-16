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
    private final ClassLoader contextClassLoader;

    ThreadLocalDelegateCallable(ThreadLocalContextManager manager, Callable<T> delegate)
    {
        this.delegate = delegate;
        this.manager = manager;
        this.context = manager.getThreadLocalContext();
        this.contextClassLoader = Thread.currentThread().getContextClassLoader();
    }

    public T call() throws Exception
    {
        ClassLoader oldContextClassLoader = Thread.currentThread().getContextClassLoader();
        try
        {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
            manager.setThreadLocalContext(context);
            return delegate.call();
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(oldContextClassLoader);
            manager.clearThreadLocalContext();
        }
    }
}
