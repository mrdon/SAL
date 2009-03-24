package com.atlassian.sal.jira.scheduling;

import com.atlassian.sal.api.scheduling.PluginJob;

import java.util.Map;

/**
 * Holds information about a PluginJob for storing in a String to descriptor Map.  This is needed because JIRA services
 * won't allow anything but Strings to be stored in its descriptor map, but we need the Map stored.
 */
public class JiraPluginSchedulerServiceDescriptor
{
    private Class<? extends PluginJob> job;
    private Map jobDataMap;

    public Class<? extends PluginJob> getJob()
    {
        return job;
    }

    public void setJob(final Class<? extends PluginJob> job)
    {
        this.job = job;
    }

    public Map getJobDataMap()
    {
        return jobDataMap;
    }

    public void setJobDataMap(final Map jobDataMap)
    {
        this.jobDataMap = jobDataMap;
    }
}
