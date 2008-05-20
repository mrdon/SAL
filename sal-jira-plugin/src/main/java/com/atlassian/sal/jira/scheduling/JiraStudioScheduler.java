package com.atlassian.sal.jira.scheduling;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.atlassian.configurable.ObjectConfiguration;
import com.atlassian.configurable.ObjectConfigurationException;
import com.atlassian.configurable.ObjectConfigurationImpl;
import com.atlassian.configurable.StringObjectDescription;
import com.atlassian.jira.service.AbstractService;
import com.atlassian.jira.service.ServiceManager;
import com.atlassian.sal.api.component.ComponentLocator;
import com.atlassian.sal.api.scheduling.StudioJob;
import com.atlassian.sal.api.scheduling.StudioScheduler;
import com.opensymphony.module.propertyset.PropertySet;

public class JiraStudioScheduler implements StudioScheduler
{
    private static final Logger log = Logger.getLogger(JiraStudioScheduler.class);
    private static final String STUDIO_JOB_NAME = "studioJobName";
    private Map<String, JiraStudioSchedulerServiceDescriptor> serviceMap;

    public JiraStudioScheduler()
    {
        serviceMap = Collections.synchronizedMap(new HashMap<String, JiraStudioSchedulerServiceDescriptor>());
    }

    public void scheduleJob(String name, Class<? extends StudioJob> job, Map jobDataMap, Date startTime,
        long repeatInterval)
    {
        ServiceManager serviceManager = ComponentLocator.getComponent(ServiceManager.class);
        // Create a map to hold the configuration for the job
        Map serviceDataMap = new HashMap();
        serviceDataMap.put(STUDIO_JOB_NAME, new String[]{name});

        // Put a service descriptor in the map
        JiraStudioSchedulerServiceDescriptor sd = new JiraStudioSchedulerServiceDescriptor();
        sd.setJob(job);
        sd.setJobDataMap(jobDataMap);
        serviceMap.put(name, sd);

        long repeatMinutes = repeatInterval / 60000L;
        try
        {
            serviceManager.addService(name,
                "com.atlassian.sal.jira.scheduling.JiraStudioScheduler$JiraStudioSchedulerService",
                repeatInterval,
                serviceDataMap);
        }
        catch (Exception e)
        {
            log.error("Error adding service to jira", e);
        }
    }

    private JiraStudioSchedulerServiceDescriptor getServiceDescriptor(String name)
    {
        return serviceMap.get(name);
    }

    private class JiraStudioSchedulerServiceDescriptor
    {
        private Class<? extends StudioJob> job;
        private Map jobDataMap;

        public Class<? extends StudioJob> getJob()
        {
            return job;
        }

        public void setJob(Class<? extends StudioJob> job)
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

    public static class JiraStudioSchedulerService extends AbstractService
    {
        private static final Logger log = Logger.getLogger(JiraStudioSchedulerService.class);
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
            JiraStudioScheduler scheduler = (JiraStudioScheduler) ComponentLocator.getComponent(StudioScheduler.class);
            JiraStudioSchedulerServiceDescriptor sd = scheduler.getServiceDescriptor(jobName);

            Class<? extends StudioJob> jobClass = sd.getJob();
            Map jobMap = sd.getJobDataMap();
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
            job.execute(jobMap);
        }

        public ObjectConfiguration getObjectConfiguration() throws ObjectConfigurationException
        {
            ObjectConfiguration oc = new ObjectConfigurationImpl(params, new StringObjectDescription(
                "Studio Scheduler Service"));
            return oc;
        }

    }
}
