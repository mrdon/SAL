package com.atlassian.sal.api.scheduling;

import java.util.Map;

/**
 * A job to be executed by the PluginScheduler.
 * <p/>
 * Implementations of this class should not store local data, as a new instance of the job is instantiated for each execution.
 * <p/>
 * Implementations of this class should also provide a default constructor with no arguments as not all job engines
 * support constructor injection.
 *
 * @since 2.0
 */
public interface PluginJob
{
    /**
     * Execute this job
     *
     * @param jobDataMap Any data the job needs to execute.  Changes to this data will be remembered between executions.
     */
    public void execute(Map<String, Object> jobDataMap);
}
