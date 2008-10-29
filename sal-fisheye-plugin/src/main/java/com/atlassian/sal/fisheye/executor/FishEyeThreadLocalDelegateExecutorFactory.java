package com.atlassian.sal.fisheye.executor;

import com.atlassian.sal.core.executor.DefaultThreadLocalDelegateExecutorFactory;

/**
 * Instance of the executor factory tailored to FishEye
 */
public class FishEyeThreadLocalDelegateExecutorFactory extends DefaultThreadLocalDelegateExecutorFactory
{
    public FishEyeThreadLocalDelegateExecutorFactory()
    {
        super(new FishEyeThreadLocalContextManager());
    }
}
