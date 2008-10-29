package com.atlassian.sal.api.executor;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Callable;

/**
 * Factory to create {@link Executor} instances that delegate to a specific Executor and
 * ensure the executed code runs in the same thread local context.
 */
public interface ThreadLocalDelegateExecutorFactory
{
    /**
     * Creates an executor that ensures the executed delegate instance runs in the same
     * thread local context as the calling code.
     *
     * @param delegate The Executor instance to delegate to
     * @return The wrapping executor that manages thread local state transfer
     */
    Executor createExecutor(Executor delegate);

    Runnable createRunnable(Runnable delegate);

    <T> Callable<T> createCallable(Callable<T> delegate);
}
