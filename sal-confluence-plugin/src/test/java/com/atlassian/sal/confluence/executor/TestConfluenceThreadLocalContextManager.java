package com.atlassian.sal.confluence.executor;

import static org.mockito.Mockito.mock;
import junit.framework.TestCase;

import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.user.User;

public class TestConfluenceThreadLocalContextManager extends TestCase
{
    private final ConfluenceThreadLocalContextManager manager = new ConfluenceThreadLocalContextManager();

    public void testGetThreadLocalContext()
    {
        final User mockUser = mock(User.class);
		AuthenticatedUserThreadLocal.setUser(mockUser);
        assertEquals(mockUser, manager.getThreadLocalContext());
    }

    public void testSetThreadLocalContext()
    {
    	final User mockUser = mock(User.class);
        manager.setThreadLocalContext(mockUser);
        assertEquals(mockUser, AuthenticatedUserThreadLocal.getUser());
    }

    public void testClearThreadLocalContext()
    {
    	final User mockUser = mock(User.class);
        AuthenticatedUserThreadLocal.setUser(mockUser);
        manager.clearThreadLocalContext();
        assertNull(AuthenticatedUserThreadLocal.getUser());
    }

    @Override
    public void tearDown()
    {
        AuthenticatedUserThreadLocal.setUser(null);
    }

}
