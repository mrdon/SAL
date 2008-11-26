package com.atlassian.sal.ctk.test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.atlassian.sal.api.scheduling.PluginJob;
import com.atlassian.sal.api.scheduling.PluginScheduler;
import com.atlassian.sal.ctk.CtkTest;
import com.atlassian.sal.ctk.CtkTestResults;

@Component
public class PluginSchedulerTest implements CtkTest
{
    private final PluginScheduler scheduler;

    public PluginSchedulerTest(final PluginScheduler scheduler)
	{
		this.scheduler = scheduler;
	}

    public void execute(final CtkTestResults results) throws InterruptedException
    {

        results.assertTrue("PluginScheduler should be injectable", scheduler != null);

        scheduler.scheduleJob("jobname", TestJob.class, new HashMap<String, Object>(), new Date(), 10000000);
        Thread.sleep(3000);

        results.assertTrue("Should be able to schedule job and have it called within 3 seconds", TestJob.called);

        scheduler.unscheduleJob("jobname");

        results.pass("Should be able to unschedule job");

        try
        {
            scheduler.unscheduleJob("jobname");
            results.fail("Should throw IllegalArgumentException when scheduling unknown job");
        } catch (final IllegalArgumentException ex)
        {
            results.pass("Should throw IllegalArgumentException when scheduling unknown job");
        }
    }

    public static class TestJob implements PluginJob
    {

        public static boolean called = false;
        public void execute(final Map<String, Object> jobDataMap)
        {
            called = true;
        }
    }
}