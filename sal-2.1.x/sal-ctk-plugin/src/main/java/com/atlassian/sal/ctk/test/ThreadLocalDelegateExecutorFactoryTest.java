package com.atlassian.sal.ctk.test;

import com.atlassian.sal.ctk.CtkTest;
import com.atlassian.sal.ctk.CtkTestResults;
import com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory;
import com.atlassian.sal.api.user.UserManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Callable;

import org.springframework.stereotype.Component;

/**
 * The thread local delegate executor factory should at least transfer the user state
 */
@Component
public class ThreadLocalDelegateExecutorFactoryTest implements CtkTest
{
    private final ThreadLocalDelegateExecutorFactory threadLocalDelegateExecutorFactory;
    private final UserManager userManager;

    public ThreadLocalDelegateExecutorFactoryTest(ThreadLocalDelegateExecutorFactory threadLocalDelegateExecutorFactory,
        UserManager userManager)
    {
        this.threadLocalDelegateExecutorFactory = threadLocalDelegateExecutorFactory;
        this.userManager = userManager;
    }

    public void execute(final CtkTestResults results) throws Exception
    {
        results.assertTrue("ThreadLocalDelegateExecutorFactory should be injectable",
            threadLocalDelegateExecutorFactory != null);
        results.assertTrue("UserManager should be injectable", userManager != null);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        ExecutorService wrappedExecutorService = threadLocalDelegateExecutorFactory.createExecutorService(
            executorService);
        Callable<String> userRetriever = new Callable<String>()
        {
            public String call() throws Exception
            {
                return userManager.getRemoteUsername();
            }
        };
        String userInCallingThread = userManager.getRemoteUsername();
        String userInExecutorThread = wrappedExecutorService.submit(userRetriever).get();
        if (userInCallingThread != null)
        {
            results.assertTrue(
                "User in executor thread not equal to user in calling thread, expected: '" + userInCallingThread +
                    "' but was '" + userInExecutorThread + "'.", userInCallingThread.equals(userInExecutorThread));
        }
        // Check that the wrapping executor cleaned up after itself, by running the same check on the unwrapped executor
        results.assertTrue("Executor thread not cleaned up", executorService.submit(userRetriever).get() == null);
    }
}
