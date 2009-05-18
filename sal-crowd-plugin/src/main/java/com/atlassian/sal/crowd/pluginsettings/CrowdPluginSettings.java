package com.atlassian.sal.crowd.pluginsettings;

import com.atlassian.crowd.manager.property.PluginPropertyManager;
import com.atlassian.sal.core.pluginsettings.AbstractStringPluginSettings;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;

/**
 * Crowd implementation of PluginSettings interface. It stores properties using crowd's salPropertyDAO.
 * There is a problem with hibernate that can't store (AFAIK) null values for a primary key fields.
 * That's why we need to translate null <code>key</code> and null <code>propertyName</code> values to String value "null".
 */
public class CrowdPluginSettings extends AbstractStringPluginSettings
{
    private static final Logger log = Logger.getLogger(CrowdPluginSettings.class);

    private static final String NULL_STRING = "null";

    private final String key;
    private final PluginPropertyManager pluginPropertyManager;

    public CrowdPluginSettings(String key, final PluginPropertyManager pluginPropertyManager)
    {
        this.key = key == null ? NULL_STRING : key;
        this.pluginPropertyManager = pluginPropertyManager;
    }

    @Override
    protected String getActual(String propertyName)
    {
        try
        {
            return pluginPropertyManager.getProperty(key, propertyName == null ? NULL_STRING : propertyName);
        }
        catch (final DataAccessException e)
        {
            log.warn(e, e);
        }
        catch (final com.atlassian.crowd.integration.exception.ObjectNotFoundException e)
        {
            // return null
        }
        return null;
    }

    @Override
    protected void putActual(String propertyName, String val)
    {
        final String notNullPropertyName = propertyName == null ? NULL_STRING : propertyName;

        pluginPropertyManager.setProperty(key, notNullPropertyName, val);
    }

    @Override
    protected Object removeActual(String propertyName)
    {
        final String notNullPropertyName = propertyName == null ? NULL_STRING : propertyName;
        Object val = getActual(notNullPropertyName);
        pluginPropertyManager.removeProperty(key, notNullPropertyName);
        return val;
    }
}