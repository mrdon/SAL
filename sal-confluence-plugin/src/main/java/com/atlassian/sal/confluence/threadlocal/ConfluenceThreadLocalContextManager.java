package com.atlassian.sal.confluence.threadlocal;

import com.atlassian.sal.api.threadlocal.ThreadLocalContextManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.user.User;

/**
 * Thread local context manager for Confluence.  The only thread local state this manager works with is the currently
 * logged in user.
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
     *
     * @param context Provided in the case that an implementation wishes to know what was set so that it knows what to
     * clear
     */
    public void clearThreadLocalContext(Object context)
    {
        AuthenticatedUserThreadLocal.reset();
    }
}
