package com.atlassian.sal.fisheye.executor;

import com.cenqua.crucible.filters.CrucibleFilter;
import junit.framework.TestCase;

import java.util.concurrent.ExecutionException;

public class TestFishEyeThreadLocalContextManager extends TestCase
{
    private FishEyeThreadLocalContextManager manager = new FishEyeThreadLocalContextManager();

    public void testGetThreadLocalContext() throws InterruptedException
    {
        CrucibleFilter.Context context = new CrucibleFilter.Context(null, null, null, null);
        CrucibleFilter.setContext(context);
        assertEquals(context, manager.getThreadLocalContext());
    }

    public void testSetThreadLocalContext() throws InterruptedException, ExecutionException
    {

        CrucibleFilter.Context context = new CrucibleFilter.Context(null, null, null, null);
        manager.setThreadLocalContext(context);
        assertEquals(context, manager.getThreadLocalContext());
    }

    public void testClearThreadLocalContext() throws InterruptedException, ExecutionException
    {
        CrucibleFilter.Context context = new CrucibleFilter.Context(null, null, null, null);
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
