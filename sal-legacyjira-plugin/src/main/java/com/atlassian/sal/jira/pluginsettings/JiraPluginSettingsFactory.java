package com.atlassian.sal.jira.pluginsettings;

import org.apache.log4j.Logger;
import org.ofbiz.core.entity.GenericValue;

import com.atlassian.core.ofbiz.util.OFBizPropertyUtils;
import com.atlassian.core.user.UserUtils;
import com.atlassian.jira.config.properties.PropertiesManager;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.user.EntityNotFoundException;
import com.opensymphony.user.User;

public class JiraPluginSettingsFactory implements PluginSettingsFactory
{

    private final ProjectManager projectManager;

    PropertiesManager pm;
    private boolean propsManagerInitialized = false;
    static Logger log = Logger.getLogger(JiraPluginSettingsFactory.class);

    public JiraPluginSettingsFactory(ProjectManager mgr)
    {
        this.projectManager = mgr;
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
            final GenericValue gv = projectManager.getProjectByKey(key);
            if (gv == null)
                throw new IllegalArgumentException("Cannot find project with key "+key);

            propertySet = OFBizPropertyUtils.getPropertySet(gv);
        }
        else
        {
            propertySet = getPropertiesManager().getPropertySet();
        }
        return new JiraPluginSettings(UnlimitedStringsPropertySet.create(propertySet));
    }

    public PluginSettings createGlobalSettings()
    {
        return createSettingsForKey(null);
    }
}
