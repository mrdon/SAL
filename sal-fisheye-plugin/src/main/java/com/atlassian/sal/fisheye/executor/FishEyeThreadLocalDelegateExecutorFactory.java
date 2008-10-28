package com.atlassian.sal.fisheye.executor;

import com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory;

import java.util.concurrent.Executor;

/**
 * Constructs a delegating executor that passes FishEye context information to the executing
 * thread correctly.
 */
public class FishEyeThreadLocalDelegateExecutorFactory implements ThreadLocalDelegateExecutorFactory
{
    public Executor createThreadLocalDelegateExector(Executor delegate)
    {
        return new FishEyeThreadLocalDelegateExecutor(delegate);
    }
}
