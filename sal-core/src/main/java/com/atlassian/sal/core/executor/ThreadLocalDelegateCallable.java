package com.atlassian.sal.core.executor;

import java.util.concurrent.Callable;

/**
 * A wrapping callable that copies the thread local state into the calling code
 *
 * @since 2.0
 */
class ThreadLocalDelegateCallable<T> implements Callable<T>
{
    private final Callable<T> delegate;
    private final ThreadLocalContextManager manager;
    private final Object context;
    private final ClassLoader contextClassLoader;

    /**
     * Create a new callable
     *
     * @param manager The context manager to get the context from
     * @param delegate The callable to delegate to
     */
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
