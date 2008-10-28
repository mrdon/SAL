package com.atlassian.sal.confluence.executor;

import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.user.User;

import java.util.concurrent.Executor;

/**
 * A delegating executor that copies the authenticated user into the executing thread.
 */
public class ConfluenceThreadLocalDelegateExecutor implements Executor
{
    private final User context;
    private final Executor delegate;

    public ConfluenceThreadLocalDelegateExecutor(Executor delegate)
    {
        this.delegate = delegate;
        this.context = getThreadLocalContext();
    }

    public void execute(Runnable runnable)
    {
        setThreadLocalContext(context);
        try
        {
            delegate.execute(runnable);
        }
        finally
        {
            clearThreadLocalContext();
        }
    }

    /**
     * Get the thread local context of the current thread
     *
     * @return The thread local context
     */
    private User getThreadLocalContext()
    {
        return AuthenticatedUserThreadLocal.getUser();
    }

    /**
     * Set the thread local context on the current thread
     *
     * @param context The context to set
     */
    private void setThreadLocalContext(Object context)
    {
        AuthenticatedUserThreadLocal.setUser((User) context);
    }

    /**
     * Clear the thread local context on the current thread
     */
    private void clearThreadLocalContext()
    {
        AuthenticatedUserThreadLocal.reset();
    }
}
