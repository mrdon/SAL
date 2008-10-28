package com.atlassian.sal.jira.executor;

import com.opensymphony.user.User;
import com.atlassian.jira.security.JiraAuthenticationContext;

import java.util.concurrent.Executor;

/**
 * Transfers the authenticated user to the execution thread
 */
class JiraThreadLocalDelegateExecutor implements Executor
{
    private final Executor delegate;
    private final User context;
    private final JiraAuthenticationContext authenticationContext;

    JiraThreadLocalDelegateExecutor(JiraAuthenticationContext authenticationContext, Executor delegate)
    {
        this.delegate = delegate;
        this.context = authenticationContext.getUser();
        this.authenticationContext = authenticationContext;
    }

    public void execute(Runnable runnable)
    {
        authenticationContext.setUser(context);
        try
        {
            delegate.execute(runnable);
        }
        finally
        {
            authenticationContext.setUser(null);
        }
    }
}
