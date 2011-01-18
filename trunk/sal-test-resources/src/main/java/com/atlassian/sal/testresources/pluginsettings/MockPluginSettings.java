package com.atlassian.sal.testresources.pluginsettings;

import com.atlassian.sal.api.pluginsettings.PluginSettings;

import java.util.Map;
import java.util.HashMap;

public class MockPluginSettings implements PluginSettings
{
    private final Map<String, Object> map = new HashMap<String, Object>();

    public Object get(String key)
    {
        return map.get(key);
    }

    public Object put(String key, Object value)
    {
        return map.put(key, value);
    }

    public Object remove(String key)
    {
        return map.remove(key);
    }
}
