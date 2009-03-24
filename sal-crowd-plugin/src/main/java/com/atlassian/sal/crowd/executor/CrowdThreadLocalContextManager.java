package com.atlassian.sal.crowd.executor;

import com.atlassian.sal.core.executor.ThreadLocalContextManager;

public class CrowdThreadLocalContextManager implements ThreadLocalContextManager
{

    public void clearThreadLocalContext()
    {
        throw new UnsupportedOperationException("CrowdThreadLocalContextManager in Crowd is not implemented.");
    }

    public Object getThreadLocalContext()
    {
        throw new UnsupportedOperationException("CrowdThreadLocalContextManager in Crowd is not implemented.");
    }

    public void setThreadLocalContext(final Object context)
    {
        throw new UnsupportedOperationException("CrowdThreadLocalContextManager in Crowd is not implemented.");
    }

}
