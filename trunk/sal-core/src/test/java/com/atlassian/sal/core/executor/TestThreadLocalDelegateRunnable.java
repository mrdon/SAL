package com.atlassian.sal.core.executor;

import junit.framework.TestCase;

public class TestThreadLocalDelegateRunnable extends TestCase
{
    public void testRun() throws InterruptedException
    {
        final ThreadLocalContextManager manager = new StubThreadLocalContextManager();
        Runnable delegate = new Runnable()
        {
            public void run()
            {
                assertNotNull(manager.getThreadLocalContext());
            }
        };

        manager.setThreadLocalContext(new Object());
        Thread t = new Thread(new ThreadLocalDelegateRunnable(manager, delegate));
        t.start();
        t.join(10000);
        assertNotNull(manager.getThreadLocalContext());
    }
}
