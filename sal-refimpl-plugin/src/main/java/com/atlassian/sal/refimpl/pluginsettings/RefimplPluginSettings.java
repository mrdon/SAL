package com.atlassian.sal.refimpl.pluginsettings;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.AbstractStringPluginSettings;

import java.util.Map;

public class RefimplPluginSettings extends AbstractStringPluginSettings
{
    private final Map<String,String> map;
    public RefimplPluginSettings(Map<String, String> map)
    {
        this.map = map;
    }

    protected void putActual(String key, String val)
    {
        map.put(key, val);
    }

    protected String getActual(String key)
    {
        return map.get(key);
    }

    protected Object removeActual(String key)
    {
        return map.remove(key);
    }
}
