package com.atlassian.sal.jira.pluginsettings;

import com.opensymphony.module.propertyset.PropertySet;
import com.atlassian.sal.core.pluginsettings.AbstractStringPluginSettings;

public class JiraPluginSettings extends AbstractStringPluginSettings
{
    private PropertySet propertySet;

    public JiraPluginSettings(PropertySet set)
    {
        this.propertySet = set;
    }

    protected void removeActual(String key)
    {
        propertySet.remove(key);
    }

    protected void putActual(String key, String val)
    {
        // remove value first - is this necessary ?
        if (key != null && propertySet.exists(key))
            propertySet.remove(key);

        propertySet.setString(key, val);
    }

    protected String getActual(String key)
    {
        if (!propertySet.exists(key) || propertySet.getType(key) != PropertySet.STRING)
            return null;
        return propertySet.getString(key);
    }

}
