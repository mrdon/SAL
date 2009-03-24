package com.atlassian.sal.crowd.executor;

import com.atlassian.sal.core.executor.ThreadLocalContextManager;

/**
 * Crowd context manager that just throws {@link UnsupportedOperationException}s
 *
 * @since 2.0.0
 */
public class CrowdThreadLocalContextManager implements ThreadLocalContextManager
{
    public Object getThreadLocalContext()
    {
        throw new UnsupportedOperationException();
    }

    public void setThreadLocalContext(Object context)
    {
        throw new UnsupportedOperationException();
    }

    public void clearThreadLocalContext()
    {
        throw new UnsupportedOperationException();
    }
}
