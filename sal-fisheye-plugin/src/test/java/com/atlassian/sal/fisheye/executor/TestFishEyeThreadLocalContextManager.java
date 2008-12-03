package com.atlassian.sal.fisheye.executor;

import java.util.concurrent.ExecutionException;

import junit.framework.TestCase;

import com.cenqua.crucible.filters.CrucibleFilter;

public class TestFishEyeThreadLocalContextManager extends TestCase
{
    private final FishEyeThreadLocalContextManager manager = new FishEyeThreadLocalContextManager();

    public void testGetThreadLocalContext() throws InterruptedException
    {
        final CrucibleFilter.Context context = new CrucibleFilter.Context(null, null);
        CrucibleFilter.setContext(context);
        assertEquals(context, manager.getThreadLocalContext());
    }

    public void testSetThreadLocalContext() throws InterruptedException, ExecutionException
    {

        final CrucibleFilter.Context context = new CrucibleFilter.Context(null, null);
        manager.setThreadLocalContext(context);
        assertEquals(context, manager.getThreadLocalContext());
    }

    public void testClearThreadLocalContext() throws InterruptedException, ExecutionException
    {
        final CrucibleFilter.Context context = new CrucibleFilter.Context(null, null);
        CrucibleFilter.setContext(context);
        manager.clearThreadLocalContext();
        assertFalse(CrucibleFilter.hasContext());
    }

    @Override
    public void tearDown()
    {
        CrucibleFilter.setContext(null);
    }
}
