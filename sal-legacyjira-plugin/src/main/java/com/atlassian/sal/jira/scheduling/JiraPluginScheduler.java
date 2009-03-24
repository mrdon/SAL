package com.atlassian.sal.jira.scheduling;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.log4j.Logger;

import com.atlassian.configurable.ObjectConfiguration;
import com.atlassian.configurable.ObjectConfigurationException;
import com.atlassian.configurable.ObjectConfigurationImpl;
import com.atlassian.configurable.ObjectConfigurationProperty;
import com.atlassian.configurable.ObjectConfigurationTypes;
import com.atlassian.configurable.StringObjectDescription;
import com.atlassian.jira.service.AbstractService;
import com.atlassian.jira.service.JiraServiceContainer;
import com.atlassian.jira.service.ServiceManager;
import com.atlassian.sal.api.component.ComponentLocator;
import com.atlassian.sal.api.scheduling.PluginJob;
import com.atlassian.sal.api.scheduling.PluginScheduler;
import com.opensymphony.module.propertyset.PropertySet;

public class JiraPluginScheduler implements PluginScheduler
{
    private static final Logger log = Logger.getLogger(JiraPluginScheduler.class);
    private static final String PLUGIN_JOB_NAME = "pluginJobName";
    private final Map<String, JiraPluginSchedulerServiceDescriptor> serviceMap;
    private final ServiceManager serviceManager;

    public JiraPluginScheduler(final ServiceManager serviceManager)
    {
        serviceMap = Collections.synchronizedMap(new HashMap<String, JiraPluginSchedulerServiceDescriptor>());
        this.serviceManager = serviceManager;
    }

    public void scheduleJob(final String name, final Class<? extends PluginJob> job, final Map<String, Object> jobDataMap, final Date startTime,
        final long repeatInterval)
    {
        // Create a map to hold the configuration for the job
        final Map<String, String[]> serviceDataMap = new HashMap<String, String[]>();
        serviceDataMap.put(PLUGIN_JOB_NAME, new String[]{name});

        // Put a service descriptor in the map
        final JiraPluginSchedulerServiceDescriptor sd = new JiraPluginSchedulerServiceDescriptor();
        sd.setJob(job);
        sd.setJobDataMap(jobDataMap);
        serviceMap.put(name, sd);

        try
        {
            // Remove the service if it exists.  We use getServices() rather than getServiceWithName() because there was
            // a bug where multiple services with the same name were being created.  getServiceWithName() will throw an
            // exception in that circumstance, so we'll just iterate through them all and delete all the ones that have
            // a matching name.
            Collection<JiraServiceContainer> services = serviceManager.getServices();
            // We have to copy the services into a second map, otherwise after deleting one, we get a
            // ConcurrentModificationException
            services = new HashSet<JiraServiceContainer>(services);
            for (final JiraServiceContainer service : services)
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
        catch (final Exception e)
        {
            log.error("Error adding service to jira", e);
        }
    }

    private JiraPluginSchedulerServiceDescriptor getServiceDescriptor(final String name)
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

    /**
     * JIRA service that executes a PluginJob
     */
    public static class JiraPluginSchedulerService extends AbstractService
    {
        private static final Logger log = Logger.getLogger(JiraPluginSchedulerService.class);
        private static final Map<String, Object> params = new HashMap<String, Object>();

        static
        {
            params.put(PLUGIN_JOB_NAME, new JiraPluginSchedulerServiceProperty());
        }

        @Override
		public void run()
        {
            PropertySet props;
            try
            {
                props = getProperties();
            }
            catch (final ObjectConfigurationException oce)
            {
                log.error("Error getting properties", oce);
                return;
            }

            final String jobName = props.getString(PLUGIN_JOB_NAME);

            // Find the descriptor
            final JiraPluginScheduler scheduler = (JiraPluginScheduler) ComponentLocator.getComponent(PluginScheduler.class);
            final JiraPluginSchedulerServiceDescriptor sd = scheduler.getServiceDescriptor(jobName);

            final Class<? extends PluginJob> jobClass = sd.getJob();
            final Map jobMap = sd.getJobDataMap();
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
            job.execute(jobMap);
        }

        public ObjectConfiguration getObjectConfiguration() throws ObjectConfigurationException
        {
            final ObjectConfiguration oc = new ObjectConfigurationImpl(params, new StringObjectDescription(
                "Plugin Scheduler Service"));
            return oc;
        }

    }

    /**
     * A hidden type, needed because you can't extend an ObjectConfigurationPropertyImpl
     */
    private static class JiraPluginSchedulerServiceProperty extends HashMap implements ObjectConfigurationProperty
    {
        public void init(final Map map)
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

        public void setI18nValues(final boolean b)
        {
        }

        public String getCascadeFrom()
        {
            return null;
        }

        public void setCascadeFrom(final String s)
        {
        }

        public boolean isEnabled()
        {
            return false;
        }
    }

	public void unscheduleJob(final String name)
	{
		// Remove the service if it exists. We use getServices() rather than
		// getServiceWithName() because there was
		// a bug where multiple services with the same name were being
		// created. getServiceWithName() will throw an
		// exception in that circumstance, so we'll just iterate through
		// them all and delete all the ones that have
		// a matching name.
		Collection<JiraServiceContainer> services = serviceManager.getServices();
		// We have to copy the services into a second map, otherwise after
		// deleting one, we get a
		// ConcurrentModificationException
		services = new HashSet<JiraServiceContainer>(services);
		for (final JiraServiceContainer service : services)
		{
			if (name.equals(service.getName()))
			{
				try
				{
					serviceManager.removeService(service.getId());
				} catch (final Exception e)
				{
					log.error("Error removing service to jira", e);
				}
			}
		}
	}

}
