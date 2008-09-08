package com.atlassian.sal.api.threadlocal;

/**
 * A class that gets and sets thread local context, so that it can be transferred between threads for asynchronous
 * execution.  Implementations are free to return any type they want for the context.
 */
public interface ThreadLocalContextManager
{
    /**
     * Get the thread local context of the current thread
     *
     * @return The thread local context
     */
    Object getThreadLocalContext();

    /**
     * Set the thread local context on the current thread
     *
     * @param context The context to set
     */
    void setThreadLocalContext(Object context);

    /**
     * Clear the thread local context on the current thread
     *
     * @param context Provided in the case that an implementation wishes to know what was set so that it knows what to
     * clear
     */
    void clearThreadLocalContext(Object context);
}
