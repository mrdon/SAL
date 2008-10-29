package com.atlassian.sal.confluence.executor;

import com.atlassian.sal.core.executor.DefaultThreadLocalDelegateExecutorFactory;

/**
 * Instance of the executor factory tailored to Confluence
 */
public class ConfluenceThreadLocalDelegateExecutorFactory extends DefaultThreadLocalDelegateExecutorFactory
{
    public ConfluenceThreadLocalDelegateExecutorFactory()
    {
        super(new ConfluenceThreadLocalContextManager());
    }
}
