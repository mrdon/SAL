package com.atlassian.sal.core.executor;

/**
 * Manager for retrieving and storing a single object which represents all thread local state
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
     */
    void clearThreadLocalContext();
}
