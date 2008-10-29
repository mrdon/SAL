package com.atlassian.sal.confluence.executor;

import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.sal.confluence.executor.ConfluenceThreadLocalContextManager;
import com.atlassian.user.User;
import com.mockobjects.dynamic.Mock;
import junit.framework.TestCase;

import java.util.concurrent.ExecutionException;

public class TestConfluenceThreadLocalContextManager extends TestCase
{
    private ConfluenceThreadLocalContextManager manager = new ConfluenceThreadLocalContextManager();

    public void testGetThreadLocalContext() throws InterruptedException
    {
        Mock mockUser = new Mock(User.class);
        AuthenticatedUserThreadLocal.setUser((User) mockUser.proxy());
        assertEquals(mockUser.proxy(), manager.getThreadLocalContext());
    }

    public void testSetThreadLocalContext() throws InterruptedException, ExecutionException
    {

        Mock mockUser = new Mock(User.class);
        manager.setThreadLocalContext(mockUser.proxy());
        assertEquals(mockUser.proxy(), AuthenticatedUserThreadLocal.getUser());
    }

    public void testClearThreadLocalContext() throws InterruptedException, ExecutionException
    {
        Mock mockUser = new Mock(User.class);
        AuthenticatedUserThreadLocal.setUser((User) mockUser.proxy());
        manager.clearThreadLocalContext();
        assertNull(AuthenticatedUserThreadLocal.getUser());
    }

    @Override
    public void tearDown()
    {
        AuthenticatedUserThreadLocal.setUser(null);
    }

}
