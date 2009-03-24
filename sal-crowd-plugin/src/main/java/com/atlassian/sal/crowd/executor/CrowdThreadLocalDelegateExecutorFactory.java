package com.atlassian.sal.crowd.executor;

import com.atlassian.sal.core.executor.DefaultThreadLocalDelegateExecutorFactory;

public class CrowdThreadLocalDelegateExecutorFactory extends DefaultThreadLocalDelegateExecutorFactory
{
    public CrowdThreadLocalDelegateExecutorFactory()
    {
        super(new CrowdThreadLocalContextManager());
    }
}