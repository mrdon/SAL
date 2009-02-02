package com.atlassian.sal.core.executor;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Callable;

/**
 * Scheduled executor service that wraps executing callables and runnables in a wrapper that transfers the thread local
 * state of the caller to the thread of the executing task.
 *
 * @since 2.0
 */
public class ThreadLocalDelegateScheduledExecutorService extends ThreadLocalDelegateExecutorService implements
    ScheduledExecutorService
{
    private final ScheduledExecutorService delegate;

    public ThreadLocalDelegateScheduledExecutorService(ThreadLocalContextManager manager,
        ScheduledExecutorService delegate)
    {
        super(manager, delegate);
        this.delegate = delegate;
    }

    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit)
    {
        return delegate.schedule(new ThreadLocalDelegateRunnable(manager, command), delay, unit);
    }

    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit)
    {
        return delegate.schedule(new ThreadLocalDelegateCallable<V>(manager, callable), delay, unit);
    }

    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit)
    {
        return delegate.scheduleAtFixedRate(new ThreadLocalDelegateRunnable(manager, command), initialDelay, period,
            unit);
    }

    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit)
    {
        return delegate.scheduleWithFixedDelay(new ThreadLocalDelegateRunnable(manager, command), initialDelay, delay,
            unit);
    }
}
