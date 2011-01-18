package com.atlassian.sal.core.executor;

/**
 * A delegating runnable that copies the thread local state into the executing thread.
 *
 * @since 2.0
 */
class ThreadLocalDelegateRunnable implements Runnable
{
    private final Object context;
    private final Runnable delegate;
    private final ThreadLocalContextManager manager;
    private final ClassLoader contextClassLoader;

    /**
     * @param manager The manager to get the context from
     * @param delegate The runnable to delegate to
     */
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
