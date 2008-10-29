package com.atlassian.sal.core.executor;

import junit.framework.TestCase;

import java.util.concurrent.Callable;

public class TestThreadLocalDelegateCallable extends TestCase
{
    public void testRun() throws InterruptedException
    {
        final ThreadLocalContextManager manager = new StubThreadLocalContextManager();
        Callable delegate = new Callable()
        {
            public Object call()
            {
                assertNotNull(manager.getThreadLocalContext());
                return null;
            }
        };

        manager.setThreadLocalContext(new Object());
        final Callable callable = new ThreadLocalDelegateCallable(manager, delegate);
        Thread t = new Thread(new Runnable()
        {

            public void run()
            {
                try
                {
                    callable.call();
                }
                catch (Exception e)
                {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });
        t.start();
        t.join(10000);
        assertNotNull(manager.getThreadLocalContext());
    }
}