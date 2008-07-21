package com.atlassian.sal.api.scheduling;

import java.util.Map;
import java.util.Date;

/**
 * Interface for scheduling jobs
 */
public interface PluginScheduler
{
    /**
     * Schedule the given job
     *
     * @param name A unique name of the job
     * @param job The class for the job
     * @param jobDataMap Any data that needs to be passed to the job.  This map instance will always be the same
* instance that is given to the job when it executes.
     * @param startTime The time the job is to start.
     * @param repeatInterval How long the interval between repeats, in milliseconds.  Note, some implementations
     */
    void scheduleJob(String name, Class<? extends PluginJob> job, Map<String, Object> jobDataMap, Date startTime,
        long repeatInterval);

    /**
     * Unschedule a job
     *
     * @param name A unique name of the job
     * @throws IllegalArgumentException If the job hasn't previously been scheduled
     */
    void unscheduleJob(String name) throws IllegalArgumentException;
}
