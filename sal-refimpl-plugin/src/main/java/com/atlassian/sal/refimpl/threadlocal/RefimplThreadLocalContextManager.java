package com.atlassian.sal.refimpl.threadlocal;

import com.atlassian.sal.api.threadlocal.ThreadLocalContextManager;

/**
 * Reference implementation uses its own thread local, most implementations won't do this.
 */
public class RefimplThreadLocalContextManager implements ThreadLocalContextManager
{
    private static ThreadLocal<Object> threadLocal = new ThreadLocal<Object>();

    /**
     * Get the thread local context of the current thread
     *
     * @return The thread local context
     */
    public Object getThreadLocalContext()
    {
        return threadLocal.get();
    }

    /**
     * Set the thread local context on the current thread
     *
     * @param context The context to set
     */
    public void setThreadLocalContext(Object context)
    {
        threadLocal.set(context);
    }

    /**
     * Clear the thread local context on the current thread
     *
     * @param context Provided in the case that an implementation wishes to know what was set so that it knows what to
     * clear
     */
    public void clearThreadLocalContext(Object context)
    {
        threadLocal.set(null);
    }
}
