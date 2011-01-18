package com.atlassian.sal.core.executor;

import junit.framework.TestCase;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.Arrays;

public class TestThreadLocalDelegateExecutor extends TestCase
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
        Executor executor = new ThreadLocalDelegateExecutor(manager, Executors.newSingleThreadExecutor());
        executor.execute(delegate);
        Thread.sleep(1000);
        assertNotNull(manager.getThreadLocalContext());
    }
}