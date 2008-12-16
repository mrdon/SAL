package com.atlassian.sal.core.executor;

import com.atlassian.sal.core.executor.ThreadLocalContextManager;

/**
 * A delegating runnable that copies the thread local state into the executing thread.
 */
class ThreadLocalDelegateRunnable implements Runnable
{
    private final Object context;
    private final Runnable delegate;
    private final ThreadLocalContextManager manager;
    private final ClassLoader contextClassLoader;

    ThreadLocalDelegateRunnable(ThreadLocalContextManager manager, Runnable delegate)
    {
        this.delegate = delegate;
        this.manager = manager;
        this.context = manager.getThreadLocalContext();
        this.contextClassLoader = Thread.currentThread().getContextClassLoader();
    }

    public void run()
    {
        ClassLoader oldContextClassLoader = Thread.currentThread().getContextClassLoader();
        try
        {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
            manager.setThreadLocalContext(context);
            delegate.run();
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(oldContextClassLoader);
            manager.clearThreadLocalContext();
        }
    }
}
