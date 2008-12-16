package com.atlassian.sal.core.executor;

import java.util.concurrent.*;
import java.util.*;

/**
 * Executor service that wraps executing callables and runnables in a wrapper that transfers the thread local state of
 * the caller to the thread of the executing task.
 */
public class ThreadLocalDelegateExecutorService extends ThreadLocalDelegateExecutor implements ExecutorService
{
    private final ExecutorService delegate;

    public ThreadLocalDelegateExecutorService(ThreadLocalContextManager manager, ExecutorService delegate)
    {
        super(manager, delegate);
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

    public <T> Future<T> submit(Callable<T> task)
    {
        return delegate.submit(new ThreadLocalDelegateCallable<T>(manager, task));
    }

    public <T> Future<T> submit(Runnable task, T result)
    {
        return delegate.submit(new ThreadLocalDelegateRunnable(manager, task), result);
    }

    public Future<?> submit(Runnable task)
    {
        return delegate.submit(new ThreadLocalDelegateRunnable(manager, task));
    }

    public <T> List<Future<T>> invokeAll(Collection<Callable<T>> tasks)
        throws InterruptedException
    {
        return delegate.invokeAll(wrapCallableList(tasks));
    }

    public <T> List<Future<T>> invokeAll(Collection<Callable<T>> tasks, long timeout, TimeUnit unit)
        throws InterruptedException
    {
        return delegate.invokeAll(wrapCallableList(tasks), timeout, unit);
    }

    public <T> T invokeAny(Collection<Callable<T>> tasks)
        throws InterruptedException, ExecutionException
    {
        return delegate.invokeAny(wrapCallableList(tasks));
    }

    public <T> T invokeAny(Collection<Callable<T>> tasks, long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException
    {
        return delegate.invokeAny(wrapCallableList(tasks), timeout, unit);
    }

    protected <T> Collection<Callable<T>> wrapCallableList(final Collection<Callable<T>> tasks)
    {
        Collection<Callable<T>> wrappedTasks = new ArrayList<Callable<T>>(tasks.size());
        for (Callable<T> task : tasks)
        {
            wrappedTasks.add(new ThreadLocalDelegateCallable<T>(manager, task));
        }
        return wrappedTasks;
    }
}
