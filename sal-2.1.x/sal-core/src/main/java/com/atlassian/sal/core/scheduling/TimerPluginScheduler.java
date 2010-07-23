package com.atlassian.sal.core.scheduling;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.atlassian.sal.api.scheduling.PluginJob;
import com.atlassian.sal.api.scheduling.PluginScheduler;

/**
 * Plugin scheduler that uses java.util.Timer
 */
public class TimerPluginScheduler implements PluginScheduler
{

    private final Map<String, Timer> tasks;

    public TimerPluginScheduler()
    {
        tasks = Collections.synchronizedMap(new HashMap<String, Timer>());
    }

    public synchronized void scheduleJob(final String name, final Class<? extends PluginJob> job, final Map<String, Object> jobDataMap, final Date startTime, final long repeatInterval)
    {
        // Use one timer per task, this will allow us to remove them if that functionality is wanted in future
        Timer timer = tasks.get(name);
        PluginTimerTask task;
        if (timer != null)
        {
            timer.cancel();
        }
        timer = new Timer("PluginSchedulerTask-" + name);
        tasks.put(name, timer);
        task = new PluginTimerTask();
        task.setJobClass(job);
        task.setJobDataMap(jobDataMap);
        timer.scheduleAtFixedRate(task, startTime, repeatInterval);
    }

    /**
     * TimerTask that executes a PluginJob
     */
    private static class PluginTimerTask extends TimerTask
    {
        private Class<? extends PluginJob> jobClass;
        private Map<String, Object> jobDataMap;
        private static final Logger log = Logger.getLogger(PluginTimerTask.class);

        @Override
		public void run()
        {
            PluginJob job;
            try
            {
                job = jobClass.newInstance();
            }
            catch (final InstantiationException ie)
            {
                log.error("Error instantiating job", ie);
                return;
            }
            catch (final IllegalAccessException iae)
            {
                log.error("Cannot access job class", iae);
                return;
            }
            job.execute(jobDataMap);
        }

        public Class<? extends PluginJob> getJobClass()
        {
            return jobClass;
        }

        public void setJobClass(final Class<? extends PluginJob> jobClass)
        {
            this.jobClass = jobClass;
        }

        public Map<String, Object> getJobDataMap()
        {
            return jobDataMap;
        }

        public void setJobDataMap(final Map<String, Object> jobDataMap)
        {
            this.jobDataMap = jobDataMap;
        }
    }

	public void unscheduleJob(final String name)
	{
        final Timer timer = tasks.remove(name);
        if (timer != null)
        {
            timer.cancel();
        }
        else
        {
            throw new IllegalArgumentException("Attempted to unschedule unknown job: " + name);
        }
    }
}
