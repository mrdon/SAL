package com.atlassian.sal.refimpl.pluginsettings;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

public class RefimplPluginSettingsFactory implements PluginSettingsFactory
{

    static Logger log = Logger.getLogger(RefimplPluginSettingsFactory.class);
    private final HashMap<String, Map<String, String>> settings = new HashMap<String,Map<String,String>>();

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

	public PluginSettings createUserSettings(String username)
	{
		throw new UnsupportedOperationException("Not implemented.");
	}
}
