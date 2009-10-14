package com.atlassian.sal.jira.pluginsettings;

import com.opensymphony.module.propertyset.PropertySet;
import com.atlassian.sal.core.pluginsettings.AbstractStringPluginSettings;

public class JiraPluginSettings extends AbstractStringPluginSettings
{
    private static final int MAX_LENGTH = 255;

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
        // remove value first - is this necessary?
        if (key != null && propertySet.exists(key))
            propertySet.remove(key);

        if (val.length() > MAX_LENGTH)
        {
            propertySet.setText(key, val);
        }
        else
        {
            propertySet.setString(key, val);
        }
    }

    protected String getActual(String key)
    {
        if (!propertySet.exists(key))
        {
            return null;
        }
        switch (propertySet.getType(key))
        {
            case PropertySet.STRING:
                return propertySet.getString(key);
            case PropertySet.TEXT:
                return propertySet.getText(key);
            default:
                return null;
        }
    }

}
