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

    ThreadLocalDelegateRunnable(ThreadLocalContextManager manager, Runnable delegate)
    {
        this.delegate = delegate;
        this.manager = manager;
        this.context = manager.getThreadLocalContext();
    }

    public void run()
    {
        manager.setThreadLocalContext(context);
        try
        {
            delegate.run();
        }
        finally
        {
            manager.clearThreadLocalContext();
        }
    }
}
