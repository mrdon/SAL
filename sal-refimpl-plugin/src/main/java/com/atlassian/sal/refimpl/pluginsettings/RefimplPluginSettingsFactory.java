package com.atlassian.sal.refimpl.pluginsettings;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class RefimplPluginSettingsFactory implements PluginSettingsFactory
{

    static Logger log = Logger.getLogger(RefimplPluginSettingsFactory.class);
    private HashMap<String, Map<String, String>> settings = new HashMap<String,Map<String,String>>();

    public PluginSettings createSettingsForKey(String key)
    {
        Map<String,String> map = settings.get(key);
        if (key == null)
            map = new HashMap<String,String>();

        return new RefimplPluginSettings(map);
    }

    public PluginSettings createGlobalSettings()
    {
        return createSettingsForKey(null);
    }
}
