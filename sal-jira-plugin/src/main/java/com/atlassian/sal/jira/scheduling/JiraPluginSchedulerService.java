package com.atlassian.sal.jira.scheduling;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.atlassian.configurable.ObjectConfiguration;
import com.atlassian.configurable.ObjectConfigurationException;
import com.atlassian.configurable.ObjectConfigurationImpl;
import com.atlassian.configurable.ObjectConfigurationProperty;
import com.atlassian.configurable.ObjectConfigurationTypes;
import com.atlassian.configurable.StringObjectDescription;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.service.AbstractService;
import com.atlassian.sal.api.scheduling.PluginJob;
import com.atlassian.sal.api.scheduling.PluginScheduler;
import com.opensymphony.module.propertyset.PropertySet;

/**
 * JIRA service that executes a PluginJob
 */
public class JiraPluginSchedulerService extends AbstractService
{
    private static final Logger log = Logger.getLogger(JiraPluginSchedulerService.class);

    private static final Map<String, Object> params = new HashMap<String, Object>();

    static
    {
        params.put(JiraPluginScheduler.PLUGIN_JOB_NAME, new JiraPluginSchedulerServiceProperty());
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

        String jobName = props.getString(JiraPluginScheduler.PLUGIN_JOB_NAME);
        if (jobName==null)
        {
            jobName = getName();
        }

        // Find the descriptor
        final JiraPluginScheduler scheduler = (JiraPluginScheduler) ComponentManager.getOSGiComponentInstanceOfType(PluginScheduler.class);
        final JiraPluginSchedulerServiceDescriptor sd = scheduler.getServiceDescriptor(jobName);
        if (sd == null)
        {
            log.error(String.format("Unable to load a service descriptor for the job '%s'. This is usually the result of an obsolete service that can removed in the Administration section.", jobName));
            return;
        }

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
        return new ObjectConfigurationImpl(params, new StringObjectDescription("Plugin Scheduler Service"));
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

}
