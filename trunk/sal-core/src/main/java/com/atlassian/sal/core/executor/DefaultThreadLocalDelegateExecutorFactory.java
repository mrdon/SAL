package com.atlassian.sal.core.executor;

import com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Creates delegating executable classes that copy thread local state
 */
public class DefaultThreadLocalDelegateExecutorFactory implements ThreadLocalDelegateExecutorFactory
{
    private final ThreadLocalContextManager manager;

    protected DefaultThreadLocalDelegateExecutorFactory(ThreadLocalContextManager manager)
    {
        this.manager = manager;
    }

    /**
     * Creates an executor that ensures the executed delegate instance runs in the same thread local context as the calling
     * code.
     *
     * @param delegate The Executor instance to delegate to
     * @return The wrapping executor that manages thread local state transfer
     */
    public Executor createExecutor(Executor delegate)
    {
        return new ThreadLocalDelegateExecutor(manager, delegate);
    }

    /**
     * Creates an executor service that ensures the executed delegate instance runs in the same thread local context as the
     * calling code.
     *
     * @param delegate The ExecutorService instance to delegate to
     * @return The wrapping ExecutorService that manages thread local state transfer
     */
    public ExecutorService createExecutorService(ExecutorService delegate)
    {
        return new ThreadLocalDelegateExecutorService(manager, delegate);
    }

    /**
     * Creates a scheduled executor service that ensures the executed delegate instance runs in the same thread local
     * context as the calling code.
     *
     * @param delegate The ScheduledExecutorService instance to delegate to
     * @return The wrapping ScheduledExecutorService that manages thread local state transfer
     */
    public ScheduledExecutorService createScheduledExecutorService(ScheduledExecutorService delegate)
    {
        return new ThreadLocalDelegateScheduledExecutorService(manager, delegate);
    }

    /**
     * Creates a runnable that ensures the executed runnable instance runs in the same thread local context as the calling
     * code
     *
     * @param delegate The runnable to delegate to
     * @return The wrapping Runnable that manages thread local state transfer
     */
    public Runnable createRunnable(Runnable delegate)
    {
        return new ThreadLocalDelegateRunnable(manager, delegate);
    }

    /**
     * Creates a callable that ensures the executed runnable instance runs in the same thread local context as the calling
     * code.
     *
     * @param delegate The callable to delegate to
     * @param <T> The type that the callable returns
     * @return The wrapping Callable that manages thread local state transfer
     */
    public <T> Callable<T> createCallable(Callable<T> delegate)
    {
        return new ThreadLocalDelegateCallable<T>(manager, delegate);
    }
}
