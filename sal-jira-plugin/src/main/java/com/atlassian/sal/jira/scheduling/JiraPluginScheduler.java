package com.atlassian.sal.jira.scheduling;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionException;

import com.atlassian.configurable.ObjectConfiguration;
import com.atlassian.configurable.ObjectConfigurationException;
import com.atlassian.configurable.ObjectConfigurationImpl;
import com.atlassian.configurable.StringObjectDescription;
import com.atlassian.jira.service.AbstractService;
import com.atlassian.jira.service.ServiceManager;
import com.atlassian.sal.api.component.ComponentLocator;
import com.atlassian.sal.api.scheduling.PluginJob;
import com.atlassian.sal.api.scheduling.PluginScheduler;
import com.opensymphony.module.propertyset.PropertySet;

public class JiraPluginScheduler implements PluginScheduler
{
    private static final Logger log = Logger.getLogger(JiraPluginScheduler.class);
    private static final String STUDIO_JOB_NAME = "pluginJobName";
    private Map<String, JiraPluginSchedulerServiceDescriptor> serviceMap;
    private ServiceManager serviceManager;

    public JiraPluginScheduler(ServiceManager serviceManager)
    {
        serviceMap = Collections.synchronizedMap(new HashMap<String, JiraPluginSchedulerServiceDescriptor>());
        this.serviceManager = serviceManager;
    }

    public void scheduleJob(String name, Class<? extends PluginJob> job, Map<String, Object> jobDataMap, Date startTime,
        long repeatInterval)
    {
        // Create a map to hold the configuration for the job
        Map serviceDataMap = new HashMap();
        serviceDataMap.put(STUDIO_JOB_NAME, new String[]{name});

        // Put a service descriptor in the map
        JiraPluginSchedulerServiceDescriptor sd = new JiraPluginSchedulerServiceDescriptor();
        sd.setJob(job);
        sd.setJobDataMap(jobDataMap);
        serviceMap.put(name, sd);

        long repeatMinutes = repeatInterval / 60000L;
        try
        {
            serviceManager.addService(name,
                "com.atlassian.sal.jira.scheduling.JiraPluginScheduler$JiraPluginSchedulerService",
                repeatInterval,
                serviceDataMap);
        }
        catch (Exception e)
        {
            log.error("Error adding service to jira", e);
        }
    }

    private JiraPluginSchedulerServiceDescriptor getServiceDescriptor(String name)
    {
        return serviceMap.get(name);
    }

    /**
     * Holds information about a PluginJob for storing in a String to descriptor Map.  This is needed because JIRA
     * services won't allow anything but Strings to be stored in its descriptor map, but we need the Map stored.
     */
    private static class JiraPluginSchedulerServiceDescriptor
    {
        private Class<? extends PluginJob> job;
        private Map jobDataMap;

        public Class<? extends PluginJob> getJob()
        {
            return job;
        }

        public void setJob(Class<? extends PluginJob> job)
        {
            this.job = job;
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

    /**
     * JIRA service that executes a PluginJob
     */
    public static class JiraPluginSchedulerService extends AbstractService
    {
        private static final Logger log = Logger.getLogger(JiraPluginSchedulerService.class);
        private static final Map params = new HashMap();

        static
        {
            params.put(STUDIO_JOB_NAME, null);
        }

        public void run()
        {
            PropertySet props;
            try
            {
                props = getProperties();
            }
            catch (ObjectConfigurationException oce)
            {
                log.error("Error getting properties", oce);
                return;
            }

            String jobName = props.getString(STUDIO_JOB_NAME);

            // Find the descriptor
            JiraPluginScheduler scheduler = (JiraPluginScheduler) ComponentLocator.getComponent(PluginScheduler.class);
            JiraPluginSchedulerServiceDescriptor sd = scheduler.getServiceDescriptor(jobName);

            Class<? extends PluginJob> jobClass = sd.getJob();
            Map jobMap = sd.getJobDataMap();
            PluginJob job;
            try
            {
                job = jobClass.newInstance();
            }
            catch (InstantiationException ie)
            {
                log.error("Error instantiating job", ie);
                return;
            }
            catch (IllegalAccessException iae)
            {
                log.error("Cannot access job class", iae);
                return;
            }
            job.execute(jobMap);
        }

        public ObjectConfiguration getObjectConfiguration() throws ObjectConfigurationException
        {
            ObjectConfiguration oc = new ObjectConfigurationImpl(params, new StringObjectDescription(
                "Plugin Scheduler Service"));
            return oc;
        }

    }
}
