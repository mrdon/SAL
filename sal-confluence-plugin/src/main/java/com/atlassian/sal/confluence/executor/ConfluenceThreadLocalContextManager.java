package com.atlassian.sal.confluence.executor;

import com.atlassian.user.User;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.sal.core.executor.ThreadLocalContextManager;

/**
 * Manages all thread local state for Confluence
 */
public class ConfluenceThreadLocalContextManager implements ThreadLocalContextManager
{
    /**
     * Get the thread local context of the current thread
     *
     * @return The thread local context
     */
    public Object getThreadLocalContext()
    {
        return AuthenticatedUserThreadLocal.getUser();
    }

    /**
     * Set the thread local context on the current thread
     *
     * @param context The context to set
     */
    public void setThreadLocalContext(Object context)
    {
        AuthenticatedUserThreadLocal.setUser((User) context);
    }

    /**
     * Clear the thread local context on the current thread
     */
    public void clearThreadLocalContext()
    {
        AuthenticatedUserThreadLocal.reset();
    }
}
