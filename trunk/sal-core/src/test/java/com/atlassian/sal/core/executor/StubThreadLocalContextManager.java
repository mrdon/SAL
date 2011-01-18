package com.atlassian.sal.core.executor;

public class StubThreadLocalContextManager implements ThreadLocalContextManager
{
    private final ThreadLocal<Object> context = new ThreadLocal<Object>();

    public Object getThreadLocalContext()
    {
        return context.get();
    }

    public void setThreadLocalContext(Object context)
    {
        this.context.set(context);
    }

    public void clearThreadLocalContext()
    {
        this.context.remove();
    }
}
