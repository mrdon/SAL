package com.atlassian.sal.ctk.test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.springframework.stereotype.Component;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.ctk.CtkTest;
import com.atlassian.sal.ctk.CtkTestResults;

@Component
public class PluginSettingsTest implements CtkTest
{
    private final PluginSettingsFactory factory;

    private static final String LONG_STRING = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

    public PluginSettingsTest(final PluginSettingsFactory factory)
	{
		this.factory = factory;
	}


    @SuppressWarnings("unchecked")
	public void execute(final CtkTestResults results)
    {
        results.assertTrue("PluginSettingsFactory should be injectable", factory != null);

        final PluginSettings settings = factory.createGlobalSettings();
        results.assertTrue("Global PluginSettings should be retrievable", settings != null);

        settings.put("string", "foo");
        results.assertTrue("Should be able to store and retrieve a string", "foo".equals(settings.get("string")));

        settings.put("longstring", LONG_STRING);
        results.assertTrue("Should be able to store and retrieve a string", LONG_STRING.equals(settings.get("longstring")));

        List<String> list = Arrays.asList("foo");
        settings.put("list", list);
        list = (List<String>) settings.get("list");
        results.assertTrue("Should be able to store and retrieve a list", list != null && "foo".equals(list.get(0)));

        Properties map = new Properties();
        map.setProperty("key", "value");
        settings.put("map", map);
        map = (Properties) settings.get("map");
        results.assertTrue("Should be able to store and retrieve a map", map != null && "value".equals(map.get("key")));

        try
        {
            settings.put("map", new HashMap());
            results.fail("Should not allow Map");
        } catch (final IllegalArgumentException ex)
        {
            results.pass("Should not allow Map");
        }
    }
}