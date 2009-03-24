package com.atlassian.sal.crowd.executor;

import com.atlassian.sal.core.executor.DefaultThreadLocalDelegateExecutorFactory;

/**
 * Simple executor factory that uses the crowd context local manager
 *
 * @since 2.0.0
 */
public class CrowdThreadLocalDelegateExecutorFactory extends DefaultThreadLocalDelegateExecutorFactory
{
    public CrowdThreadLocalDelegateExecutorFactory()
    {
        super(new CrowdThreadLocalContextManager());
    }
}
