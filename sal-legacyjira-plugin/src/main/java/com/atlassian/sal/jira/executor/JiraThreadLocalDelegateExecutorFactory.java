package com.atlassian.sal.jira.executor;

import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.sal.core.executor.DefaultThreadLocalDelegateExecutorFactory;

/**
 * Instance of the delegate executor factory tailored to JIRA
 */
public class JiraThreadLocalDelegateExecutorFactory extends DefaultThreadLocalDelegateExecutorFactory
{
    public JiraThreadLocalDelegateExecutorFactory(JiraAuthenticationContext authenticationContext)
    {
        super(new JiraThreadLocalContextManager(authenticationContext));
    }
}
