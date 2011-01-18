package com.atlassian.sal.testresources.pluginsettings;

import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.pluginsettings.PluginSettings;

import java.util.Map;
import java.util.HashMap;

public class MockPluginSettingsFactory implements PluginSettingsFactory
{
    private final Map<String, PluginSettings> map = new HashMap<String, PluginSettings>();

    public PluginSettings createSettingsForKey(String key)
    {
        PluginSettings pluginSettings = map.get(key);
        if (pluginSettings == null)
        {
            pluginSettings = new MockPluginSettings();
            map.put(key, pluginSettings);
        }
        return pluginSettings;
    }

    public PluginSettings createGlobalSettings()
    {
        return createSettingsForKey(null);
    }

    
}
