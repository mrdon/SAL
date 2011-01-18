package com.atlassian.sal.core.pluginsettings;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.core.util.Assert;

/**
 * PluginSettings implementation that delegates to another PluginSettings, adding a prefix to every key passed in
 */
public class PrefixedPluginSettingsDelegate implements PluginSettings
{
    private final String prefix;
    private final PluginSettings target;

    public PrefixedPluginSettingsDelegate(String prefix, PluginSettings target)
    {
        Assert.notNull(prefix, "Prefix must not be null");
        Assert.notNull(target, "Target must not be null");
        this.prefix = prefix;
        this.target = target;
    }

    public Object get(String key)
    {
        return target.get(prefix + key);
    }

    public Object put(String key, Object value)
    {
        return target.put(prefix + key, value);
    }

    public Object remove(String key)
    {
        return target.remove(prefix + key);
    }
}
