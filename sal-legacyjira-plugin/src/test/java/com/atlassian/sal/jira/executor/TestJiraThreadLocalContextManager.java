package com.atlassian.sal.jira.executor;

import com.atlassian.jira.security.JiraAuthenticationContext;
import com.mockobjects.dynamic.Mock;
import junit.framework.TestCase;

public class TestJiraThreadLocalContextManager extends TestCase
{
    public void testGetThreadLocalContext() throws InterruptedException
    {
        Mock mockAuthenticationContext = new Mock(JiraAuthenticationContext.class);
        JiraThreadLocalContextManager manager = new JiraThreadLocalContextManager((JiraAuthenticationContext) mockAuthenticationContext.proxy());
        mockAuthenticationContext.expectAndReturn("getUser", null);
        assertNull(manager.getThreadLocalContext());
        mockAuthenticationContext.verify();
    }

    /* Commented out due to mockobjects bug when passing null
    public void testSetThreadLocalContext() throws Exception, ExecutionException, ImmutableException, DuplicateEntityException
    {
        Mock mockAuthenticationContext = new Mock(JiraAuthenticationContext.class);
        JiraThreadLocalContextManager manager = new JiraThreadLocalContextManager((JiraAuthenticationContext) mockAuthenticationContext.proxy());
        mockAuthenticationContext.expectAndReturn("setUser", C.args(C.IS_NULL));
        manager.setThreadLocalContext(null);
        mockAuthenticationContext.verify();
    }

    public void testClearThreadLocalContext() throws InterruptedException, ExecutionException
    {
        Mock mockAuthenticationContext = new Mock(JiraAuthenticationContext.class);
        JiraThreadLocalContextManager manager = new JiraThreadLocalContextManager((JiraAuthenticationContext) mockAuthenticationContext.proxy());
        mockAuthenticationContext.expectAndReturn("setUser", C.args(C.IS_NULL));
        manager.setThreadLocalContext(null);
        mockAuthenticationContext.verify();
    }
    */
}
