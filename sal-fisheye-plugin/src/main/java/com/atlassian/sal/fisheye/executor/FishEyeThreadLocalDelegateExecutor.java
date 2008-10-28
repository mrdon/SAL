package com.atlassian.sal.fisheye.executor;

import com.cenqua.crucible.filters.CrucibleFilter;

import java.util.concurrent.Executor;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * Creates a delegating executor that ensures the context is passed to the executing thread
 */
class FishEyeThreadLocalDelegateExecutor implements Executor
{
    private final Executor delegate;
    private final Object context;

    FishEyeThreadLocalDelegateExecutor(Executor delegate)
    {
        this.delegate = delegate;
        this.context = getThreadLocalContext();
    }

    public void execute(Runnable runnable)
    {
        setThreadLocalContext(context);
        try
        {
            delegate.execute(runnable);
        }
        finally
        {
            clearThreadLocalContext();
        }
    }



    /**
     * Get the thread local context of the current thread
     *
     * @return The thread local context
     */
    private Object getThreadLocalContext()
    {
        // Basically, FishEye sucks.  This is bad, but it will work.  Let me say again... FishEye sucks.
        // Returning this much context probably also isn't thread safe... but as long as it's used to only get users :) :)
        // Here be dragons
        try
        {
            Method method = CrucibleFilter.class.getDeclaredMethod("getContext");
            method.setAccessible(true);
            return method.invoke(null);
        }
        catch (IllegalAccessException iae)
        {
            // This one shouldn't happen
            throw new RuntimeException("I couldn't access a method that I set as accessible", iae);
        }
        catch (IllegalArgumentException iarge)
        {
            // Again, this one shouldn't happen
            throw new RuntimeException("I thought a called a noargs static method, was I wrong?", iarge);
        }
        catch (NoSuchMethodException nsme)
        {
            throw new RuntimeException("Somebody changed FishEye", nsme);
        }
        catch (InvocationTargetException ite)
        {
            // Now, this one is possible
            throw new RuntimeException("Can't get the Crucible context", ite.getCause());
        }
    }

    /**
     * Set the thread local context on the current thread
     *
     * @param context The context to set
     */
    private void setThreadLocalContext(Object context)
    {
        // How nice of them to provide such a convenient method for setting it, but not getting it.
        CrucibleFilter.setContext((CrucibleFilter.Context) context);
    }

    /**
     * Remove the given context from the current thread
     */
    private void clearThreadLocalContext()
    {
        CrucibleFilter.setContext(null);
    }
}
