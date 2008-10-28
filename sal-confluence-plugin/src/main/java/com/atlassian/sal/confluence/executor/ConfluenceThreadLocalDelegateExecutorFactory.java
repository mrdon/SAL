package com.atlassian.sal.confluence.executor;

import com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory;

import java.util.concurrent.Executor;

/**
 * Creates a delegating executor that copies Confluence thread local state
 */
public class ConfluenceThreadLocalDelegateExecutorFactory implements ThreadLocalDelegateExecutorFactory
{
    public Executor createThreadLocalDelegateExector(Executor delegate)
    {
        return new ConfluenceThreadLocalDelegateExecutor(delegate);
    }
}
