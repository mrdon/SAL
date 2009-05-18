package com.atlassian.sal.crowd.pluginsettings;

import com.atlassian.crowd.manager.property.PluginPropertyManager;
import com.atlassian.sal.api.component.ComponentLocator;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

public class CrowdPluginSettingsFactory implements PluginSettingsFactory
{
    private PluginPropertyManager pluginPropertyManager;

    public CrowdPluginSettingsFactory()
    {
    }

    public PluginSettings createGlobalSettings()
    {
        return new CrowdPluginSettings(null, getSalPropertyDao());
    }

    public PluginSettings createSettingsForKey(String key)
    {
        return new CrowdPluginSettings(key, getSalPropertyDao());
    }

    private PluginPropertyManager getSalPropertyDao()
    {
        if (this.pluginPropertyManager == null)
        {
            this.pluginPropertyManager = ComponentLocator.getComponent(PluginPropertyManager.class);
        }
        return this.pluginPropertyManager;
    }
}
