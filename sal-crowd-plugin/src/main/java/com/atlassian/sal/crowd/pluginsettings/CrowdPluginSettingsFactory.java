package com.atlassian.sal.crowd.pluginsettings;

import com.atlassian.crowd.model.salproperty.SALPropertyDAO;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

public class CrowdPluginSettingsFactory implements PluginSettingsFactory
{
    private final SALPropertyDAO salPropertyDAO;

    public CrowdPluginSettingsFactory(final SALPropertyDAO salPropertyDAO)
    {
        this.salPropertyDAO = salPropertyDAO;
    }

    public PluginSettings createGlobalSettings()
    {
        return new CrowdPluginSettings(null, this.salPropertyDAO);
    }

    public PluginSettings createSettingsForKey(String key)
    {
        return new CrowdPluginSettings(key, this.salPropertyDAO);
    }
}
