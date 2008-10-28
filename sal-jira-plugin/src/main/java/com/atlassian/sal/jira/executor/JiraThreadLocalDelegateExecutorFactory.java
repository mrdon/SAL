package com.atlassian.sal.jira.executor;

import com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory;
import com.atlassian.jira.security.JiraAuthenticationContext;

import java.util.concurrent.Executor;

/**
 * Creates a delegating executor that transfers all JIRA thread local state
 */
public class JiraThreadLocalDelegateExecutorFactory implements ThreadLocalDelegateExecutorFactory
{
    private final JiraAuthenticationContext jiraAuthenticationContext;

    public JiraThreadLocalDelegateExecutorFactory(JiraAuthenticationContext jiraAuthenticationContext)
    {
        this.jiraAuthenticationContext = jiraAuthenticationContext;
    }

    public Executor createThreadLocalDelegateExector(Executor delegate)
    {
        return new JiraThreadLocalDelegateExecutor(jiraAuthenticationContext, delegate);
    }
}
