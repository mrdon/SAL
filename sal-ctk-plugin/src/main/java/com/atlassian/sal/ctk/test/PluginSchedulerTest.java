package com.atlassian.sal.ctk.test;

import com.atlassian.sal.ctk.CtkTest;
import com.atlassian.sal.ctk.CtkTestResults;
import com.atlassian.sal.api.component.ComponentLocator;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.scheduling.PluginScheduler;
import com.atlassian.sal.api.scheduling.PluginJob;
import com.atlassian.plugin.PluginManager;

import java.util.*;

import org.springframework.stereotype.Component;

@Component
public class PluginSchedulerTest implements CtkTest
{
    private final PluginScheduler scheduler;

    public PluginSchedulerTest(PluginScheduler scheduler) {this.scheduler = scheduler;}

    public void execute(CtkTestResults results) throws InterruptedException
    {

        results.assertTrue("PluginScheduler should be injectable", scheduler != null);

        scheduler.scheduleJob("jobname", TestJob.class, new HashMap(), new Date(), 10000000);
        Thread.sleep(3000);

        results.assertTrue("Should be able to schedule job and have it called within 3 seconds", TestJob.called);

        scheduler.unscheduleJob("jobname");

        results.pass("Should be able to unschedule job");

        try
        {
            scheduler.unscheduleJob("jobname");
            results.fail("Should throw IllegalArgumentException when scheduling unknown job");
        } catch (IllegalArgumentException ex)
        {
            results.pass("Should throw IllegalArgumentException when scheduling unknown job");
        }
    }

    public static class TestJob implements PluginJob
    {

        public static boolean called = false;
        public void execute(Map<String, Object> jobDataMap)
        {
            called = true;
        }
    }
}