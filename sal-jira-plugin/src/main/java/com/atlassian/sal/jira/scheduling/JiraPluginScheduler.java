package com.atlassian.sal.jira.scheduling;

import java.util.*;

import org.apache.log4j.Logger;

import com.atlassian.configurable.*;
import com.atlassian.jira.service.AbstractService;
import com.atlassian.jira.service.ServiceManager;
import com.atlassian.jira.service.JiraServiceContainer;
import com.atlassian.sal.api.scheduling.PluginJob;
import com.atlassian.sal.api.scheduling.PluginScheduler;
import com.opensymphony.module.propertyset.PropertySet;

public class JiraPluginScheduler implements PluginScheduler
{
    private static final Logger log = Logger.getLogger(JiraPluginScheduler.class);
    private static final String PLUGIN_JOB_NAME = "pluginJobName";
    private Map<String, JiraPluginSchedulerServiceDescriptor> serviceMap;
    private ServiceManager serviceManager;

    // Lame way to allow the JIRA service to access the scheduler
    private static JiraPluginScheduler SELF;

    public JiraPluginScheduler(ServiceManager serviceManager)
    {
        serviceMap = Collections.synchronizedMap(new HashMap<String, JiraPluginSchedulerServiceDescriptor>());
        this.serviceManager = serviceManager;
        synchronized(JiraPluginScheduler.class)
        {
            if (SELF != null)
                throw new IllegalStateException("There shouldn't be two schedulers");
            SELF = this;
        }
    }

    public void scheduleJob(String name, Class<? extends PluginJob> job, Map<String, Object> jobDataMap, Date startTime,
        long repeatInterval)
    {
        // Create a map to hold the configuration for the job
        Map serviceDataMap = new HashMap();
        serviceDataMap.put(PLUGIN_JOB_NAME, new String[]{name});

        // Put a service descriptor in the map
        JiraPluginSchedulerServiceDescriptor sd = new JiraPluginSchedulerServiceDescriptor();
        sd.setJob(job);
        sd.setJobDataMap(jobDataMap);
        serviceMap.put(name, sd);

        long repeatMinutes = repeatInterval / 60000L;
        try
        {
            // Remove the service if it exists.  We use getServices() rather than getServiceWithName() because there was
            // a bug where multiple services with the same name were being created.  getServiceWithName() will throw an
            // exception in that circumstance, so we'll just iterate through them all and delete all the ones that have
            // a matching name.
            Collection<JiraServiceContainer> services = serviceManager.getServices();
            // We have to copy the services into a second map, otherwise after deleting one, we get a
            // ConcurrentModificationException
            services = new HashSet(services);
            for (JiraServiceContainer service : services)
            {
                if (name.equals(service.getName()))
                {
                    serviceManager.removeService(service.getId());
                }
            }
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

    public void unscheduleJob(String name)
    {
        try
        {
            if (serviceManager.getServiceWithName(name) == null)
                throw new IllegalArgumentException("Invalid job: "+ name);
            
            serviceManager.removeServiceByName(name);
        } catch (Exception e)
        {
            log.error("Unable to remove service", e);
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
            params.put(PLUGIN_JOB_NAME, new JiraPluginSchedulerServiceProperty());
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

            String jobName = props.getString(PLUGIN_JOB_NAME);

            // Find the descriptor
            JiraPluginScheduler scheduler = SELF;
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

    /**
     * A hidden type, needed because you can't extend an ObjectConfigurationPropertyImpl
     */
    private static class JiraPluginSchedulerServiceProperty extends HashMap implements ObjectConfigurationProperty
    {
        public void init(Map map)
        {
        }

        public String getName()
        {
            return null;
        }

        public String getDescription()
        {
            return null;
        }

        public String getDefault()
        {
            return null;
        }

        public int getType()
        {
            return ObjectConfigurationTypes.HIDDEN;
        }

        public boolean isI18nValues()
        {
            return false;
        }

        public void setI18nValues(boolean b)
        {
        }

        public String getCascadeFrom()
        {
            return null;
        }

        public void setCascadeFrom(String s)
        {
        }

        public boolean isEnabled()
        {
            return false;
        }
    }
}
