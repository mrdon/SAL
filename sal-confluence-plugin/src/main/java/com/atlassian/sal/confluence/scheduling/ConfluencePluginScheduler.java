package com.atlassian.sal.confluence.scheduling;

import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;

import com.atlassian.sal.api.component.ComponentLocator;
import com.atlassian.sal.api.scheduling.PluginJob;
import com.atlassian.sal.api.scheduling.PluginScheduler;

/**
 * Confluence plugin scheduler, uses Quartz
 */
public class ConfluencePluginScheduler implements PluginScheduler
{

    private static final String JOB_CLASS_KEY = "pluginJobClass";
    private static final String JOB_DATA_MAP_KEY = "pluginJobDataMap";
    private static final Logger log = Logger.getLogger(ConfluencePluginScheduler.class);

    public void scheduleJob(String name, Class<? extends PluginJob> job, Map<String, Object> jobDataMap, Date startTime,
        long repeatInterval)
    {
        // Get the scheduler
        Scheduler scheduler = ComponentLocator.getComponent(Scheduler.class);
        // Create a new job detail
        JobDetail jobDetail = new JobDetail();
        jobDetail.setGroup("pluginSchedulerJobGroup");
        jobDetail.setName(name);
        jobDetail.setJobClass(ConfluencePluginJob.class);
        JobDataMap jobDetailMap = new JobDataMap();
        jobDetailMap.put("runOncePerCluster", "false");
        jobDetailMap.put(JOB_CLASS_KEY, job);
        jobDetailMap.put(JOB_DATA_MAP_KEY, jobDataMap);
        jobDetail.setJobDataMap(jobDetailMap);

        // Create a new trigger
        SimpleTrigger trigger = new SimpleTrigger();
        trigger.setGroup("pluginSchedulerTriggerGroup");
        trigger.setName(name + "Trigger");
        if (startTime != null)
        {
            trigger.setStartTime(startTime);
        }
        if (repeatInterval == 0)
        {
            trigger.setRepeatCount(0);
        }
        else
        {
            trigger.setRepeatInterval(repeatInterval);
            trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        }

        // Schedule job
        try
        {
            scheduler.scheduleJob(jobDetail, trigger);
        }
        catch (SchedulerException se)
        {
            log.error("Error scheduling job", se);
        }
    }

    /**
     * A Quartz job that executes a PluginJob
     */
    public static class ConfluencePluginJob implements Job
    {
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException
        {
            JobDataMap map = jobExecutionContext.getJobDetail().getJobDataMap();
            Class<? extends PluginJob> jobClass = (Class<? extends PluginJob>) map.get(JOB_CLASS_KEY);
            Map pluginJobMap = (Map) map.get(JOB_DATA_MAP_KEY);
            // Instantiate the job
            PluginJob job;
            try
            {
                job = jobClass.newInstance();
            }
            catch (InstantiationException ie)
            {
                throw new JobExecutionException("Error instantiating job", ie, false);
            }
            catch (IllegalAccessException iae)
            {
                throw new JobExecutionException("Cannot access job class", iae, false);
            }
            job.execute(pluginJobMap);
        }
    }

}