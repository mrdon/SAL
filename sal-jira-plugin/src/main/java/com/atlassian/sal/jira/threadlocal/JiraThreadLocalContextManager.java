package com.atlassian.sal.jira.threadlocal;

import com.atlassian.sal.api.threadlocal.ThreadLocalContextManager;
import com.atlassian.sal.api.component.ComponentLocator;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.opensymphony.user.User;

/**
 * JIRA thread local context manager.  The thread local state that this manager currently supports is the current user.
 */
public class JiraThreadLocalContextManager implements ThreadLocalContextManager
{
    private final JiraAuthenticationContext authenticationContext;

    public JiraThreadLocalContextManager(JiraAuthenticationContext authenticationContext)
    {
        this.authenticationContext = authenticationContext;
    }

    /**
     * Get the thread local context of the current thread
     *
     * @return The thread local context
     */
    public Object getThreadLocalContext()
    {
        return authenticationContext.getUser();
    }

    /**
     * Set the thread local context on the current thread
     *
     * @param context The context to set
     */
    public void setThreadLocalContext(Object context)
    {
        authenticationContext.setUser((User) context);
    }

    /**
     * Clear the thread local context on the current thread
     *
     * @param context Provided in the case that an implementation wishes to know what was set so that it knows what to
     * clear
     */
    public void clearThreadLocalContext(Object context)
    {
        authenticationContext.setUser(null);
    }

}
