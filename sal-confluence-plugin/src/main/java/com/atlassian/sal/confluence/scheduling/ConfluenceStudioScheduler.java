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
import com.atlassian.sal.api.scheduling.StudioJob;
import com.atlassian.sal.api.scheduling.StudioScheduler;

public class ConfluenceStudioScheduler implements StudioScheduler
{

    private static final String STUDIO_JOB_CLASS_KEY = "studioJobClass";
    private static final String STUDIO_JOB_DATA_MAP_KEY = "studioJobDataMap";
    private static final Logger log = Logger.getLogger(ConfluenceStudioScheduler.class);

    public void scheduleJob(String name, Class<? extends StudioJob> job, Map jobDataMap, Date startTime,
        long repeatInterval)
    {
        // Get the scheduler
        Scheduler scheduler = ComponentLocator.getComponent(Scheduler.class);
        // Create a new job detail
        JobDetail jobDetail = new JobDetail();
        jobDetail.setGroup("studioSchedulerJobGroup");
        jobDetail.setName(name);
        jobDetail.setJobClass(ConfluenceStudioJob.class);
        JobDataMap jobDetailMap = new JobDataMap();
        jobDetailMap.put("runOncePerCluster", "false");
        jobDetailMap.put(STUDIO_JOB_CLASS_KEY, job);
        jobDetailMap.put(STUDIO_JOB_DATA_MAP_KEY, jobDataMap);
        jobDetail.setJobDataMap(jobDetailMap);

        // Create a new trigger
        SimpleTrigger trigger = new SimpleTrigger();
        trigger.setGroup("studioSchedulerTriggerGroup");
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

    public static class ConfluenceStudioJob implements Job
    {
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException
        {
            JobDataMap map = jobExecutionContext.getJobDetail().getJobDataMap();
            Class<? extends StudioJob> jobClass = (Class<? extends StudioJob>) map.get(STUDIO_JOB_CLASS_KEY);
            Map studioJobMap = (Map) map.get(STUDIO_JOB_DATA_MAP_KEY);
            // Instantiate the job
            StudioJob job;
            try
            {
                job = jobClass.newInstance();
            }
            catch (Exception e)
            {
                throw new JobExecutionException("Error instantiating studio job", e, false);
            }
            job.execute(studioJobMap);
        }


    }

}
