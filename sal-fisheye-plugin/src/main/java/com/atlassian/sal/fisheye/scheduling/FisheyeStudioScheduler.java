package com.atlassian.sal.fisheye.scheduling;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.atlassian.sal.api.scheduling.StudioJob;
import com.atlassian.sal.api.scheduling.StudioScheduler;


public class FisheyeStudioScheduler implements StudioScheduler
{

    private Map<String, Timer> tasks;

    public FisheyeStudioScheduler()
    {
        tasks = Collections.synchronizedMap(new HashMap<String, Timer>());
    }

    public synchronized void scheduleJob(String name, Class<? extends StudioJob> job, Map jobDataMap, Date startTime,
        long repeatInterval)
    {
        // Use one timer per task, this will allow us to remove them if that functionality is wanted in future
        Timer timer = tasks.get(name);
        FisheyeStudioTimerTask task;
        if (timer != null)
        {
            timer.cancel();
        }
        timer = new Timer("FisheyeStudioSchedulerTask-" + name);
        tasks.put(name, timer);
        task = new FisheyeStudioTimerTask();
        task.setJobClass(job);
        task.setJobDataMap(jobDataMap);
        timer.scheduleAtFixedRate(task, startTime, repeatInterval);
    }

    private static class FisheyeStudioTimerTask extends TimerTask
    {
        private Class<? extends StudioJob> jobClass;
        private Map jobDataMap;
        private static final Logger log = Logger.getLogger(FisheyeStudioTimerTask.class);

        public void run()
        {
            StudioJob job;
            try
            {
                job = jobClass.newInstance();
            }
            catch (Exception e)
            {
                log.error("Error instantiating job", e);
                return;
            }
            job.execute(jobDataMap);
        }

        public Class<? extends StudioJob> getJobClass()
        {
            return jobClass;
        }

        public void setJobClass(Class<? extends StudioJob> jobClass)
        {
            this.jobClass = jobClass;
        }

        public Map getJobDataMap()
        {
            return jobDataMap;
        }

        public void setJobDataMap(Map jobDataMap)
        {
            this.jobDataMap = jobDataMap;
        }
    }
}
