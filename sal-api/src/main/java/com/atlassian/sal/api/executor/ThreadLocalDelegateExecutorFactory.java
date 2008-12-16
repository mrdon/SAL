package com.atlassian.sal.api.executor;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Factory to create {@link Executor} instances that delegate to a specific Executor and ensure the executed code runs
 * in the same thread local context.
 */
public interface ThreadLocalDelegateExecutorFactory
{
    /**
     * Creates an executor that ensures the executed delegate instance runs in the same thread local context as the
     * calling code.
     *
     * @param delegate The Executor instance to delegate to
     * @return The wrapping executor that manages thread local state transfer
     */
    Executor createExecutor(Executor delegate);

    /**
     * Creates an executor service that ensures the executed delegate instance runs in the same thread local context as
     * the calling code.
     *
     * @param delegate The ExecutorService instance to delegate to
     * @return The wrapping ExecutorService that manages thread local state transfer
     */
    ExecutorService createExecutorService(ExecutorService delegate);

    /**
     * Creates a scheduled executor service that ensures the executed delegate instance runs in the same thread local
     * context as the calling code.
     *
     * @param delegate The ScheduledExecutorService instance to delegate to
     * @return The wrapping ScheduledExecutorService that manages thread local state transfer
     */
    ScheduledExecutorService createScheduledExecutorService(ScheduledExecutorService delegate);

    /**
     * Creates a runnable that ensures the executed runnable instance runs in the same thread local context as the
     * calling code
     *
     * @param delegate The runnable to delegate to
     * @return The wrapping Runnable that manages thread local state transfer
     */
    Runnable createRunnable(Runnable delegate);

    /**
     * Creates a callable that ensures the executed runnable instance runs in the same thread local context as the
     * calling code.
     *
     * @param delegate The callable to delegate to
     * @param <T> The type that the callable returns
     * @return The wrapping Callable that manages thread local state transfer
     */
    <T> Callable<T> createCallable(Callable<T> delegate);
}
