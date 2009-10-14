package com.atlassian.sal.jira.pluginsettings;

import com.atlassian.jira.config.properties.PropertiesManager;
import com.atlassian.jira.propertyset.JiraPropertySetFactory;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.opensymphony.module.propertyset.PropertySet;
import org.apache.log4j.Logger;

public class JiraPluginSettingsFactory implements PluginSettingsFactory
{

    private final JiraPropertySetFactory jiraPropertySetFactory;
    private final ProjectManager projectManager;

    PropertiesManager pm;
    private boolean propsManagerInitialized = false;
    static Logger log = Logger.getLogger(JiraPluginSettingsFactory.class);

    public JiraPluginSettingsFactory(JiraPropertySetFactory jiraPropertySetFactory, ProjectManager projectManager)
    {
        this.jiraPropertySetFactory = jiraPropertySetFactory;
        this.projectManager = projectManager;
        pm = getPropertiesManager();
    }

    private PropertiesManager getPropertiesManager()
    {
        if (pm == null)
        {
            if (!propsManagerInitialized)
            {
                try
                {
                    pm = PropertiesManager.getInstance();
                }
                catch (final UnsupportedOperationException e)
                {
                    // database can be locked
                    log.warn("unable to get a PropertiesManager!");
                }
                propsManagerInitialized = true;
            }
        }

        return pm;
    }

    public PluginSettings createSettingsForKey(String key)
    {
        PropertySet propertySet;
        if (key != null)
        {
            propertySet = LazyProjectMigratingPropertySet.create(projectManager, jiraPropertySetFactory,
                jiraPropertySetFactory.buildCachingDefaultPropertySet(key, true), key);
        }
        else
        {
            propertySet = getPropertiesManager().getPropertySet();
        }
        return new JiraPluginSettings(propertySet);
    }

    public PluginSettings createGlobalSettings()
    {
        return createSettingsForKey(null);
    }
}
