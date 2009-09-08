package com.atlassian.sal.jira.scheduling;

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
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

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

        final String jobName = props.getString(JiraPluginScheduler.PLUGIN_JOB_NAME);

        // Find the descriptor
        final JiraPluginScheduler scheduler = (JiraPluginScheduler) ComponentManager.getOSGiComponentInstanceOfType(PluginScheduler.class);
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
        }d
        job.execute(jobMap);
    }

    public ObjectConfiguration getObjectConfiguration() throws ObjectConfigurationException
    {
        final ObjectConfiguration oc = new ObjectConfigurationImpl(params, new StringObjectDescription(
            "Plugin Scheduler Service"));
        return oc;
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
