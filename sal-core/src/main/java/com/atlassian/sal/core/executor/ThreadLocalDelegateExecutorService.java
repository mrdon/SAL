package com.atlassian.sal.core.executor;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Executor service that wraps executing callables and runnables in a wrapper that transfers the thread local state of
 * the caller to the thread of the executing task.
 *
 * @since 2.0
 */
public class ThreadLocalDelegateExecutorService extends AbstractExecutorService
{
    protected final ThreadLocalContextManager manager;
    private final ExecutorService delegate;

    public ThreadLocalDelegateExecutorService(ThreadLocalContextManager manager, ExecutorService delegate)
    {
        this.manager = manager;
        this.delegate = delegate;
    }

    public void shutdown()
    {
        delegate.shutdown();
    }

    public List<Runnable> shutdownNow()
    {
        return delegate.shutdownNow();
    }

    public boolean isShutdown()
    {
        return delegate.isShutdown();
    }

    public boolean isTerminated()
    {
        return delegate.isTerminated();
    }

    public boolean awaitTermination(long timeout, TimeUnit unit)
        throws InterruptedException
    {
        return delegate.awaitTermination(timeout, unit);
    }

    public void execute(Runnable command)
    {
        delegate.execute(new ThreadLocalDelegateRunnable(manager, command));
    }
}
