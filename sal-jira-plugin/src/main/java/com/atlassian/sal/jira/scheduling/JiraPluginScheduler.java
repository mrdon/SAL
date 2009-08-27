package com.atlassian.sal.jira.scheduling;

import com.atlassian.jira.service.JiraServiceContainer;
import com.atlassian.jira.service.ServiceManager;
import com.atlassian.sal.api.scheduling.PluginJob;
import com.atlassian.sal.api.scheduling.PluginScheduler;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class JiraPluginScheduler implements PluginScheduler
{
    private static final Logger log = Logger.getLogger(JiraPluginScheduler.class);

    public static final String PLUGIN_JOB_NAME = "pluginJobName";
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
        serviceDataMap.put(PLUGIN_JOB_NAME, new String[] { name });

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
            // SAL-81 Use the Class version of addService() to avoid possible ClassNotFoundException. 
            serviceManager.addService(name,
                    JiraPluginSchedulerService.class,
                    repeatInterval,
                    serviceDataMap);
        }
        catch (final Exception e)
        {
            log.error("Error adding service to jira", e);
        }
    }

    JiraPluginSchedulerServiceDescriptor getServiceDescriptor(final String name)
    {
        return serviceMap.get(name);
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
                }
                catch (final Exception e)
                {
                    log.error("Error removing service to jira", e);
                }
            }
        }
    }

}
