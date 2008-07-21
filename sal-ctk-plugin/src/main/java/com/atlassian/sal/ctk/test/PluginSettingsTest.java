package com.atlassian.sal.ctk.test;

import com.atlassian.sal.ctk.CtkTest;
import com.atlassian.sal.ctk.CtkTestResults;
import com.atlassian.sal.api.component.ComponentLocator;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.plugin.PluginManager;

import java.util.*;

import org.springframework.stereotype.Component;

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

    public PluginSettingsTest(PluginSettingsFactory factory) {this.factory = factory;}


    public void execute(CtkTestResults results)
    {
        results.assertTrue("PluginSettingsFactory should be injectable", factory != null);

        PluginSettings settings = factory.createGlobalSettings();
        results.assertTrue("Global PluginSettings should be retrievable", settings != null);

        settings.put("string", "foo");
        results.assertTrue("Should be able to store and retrieve a string", "foo".equals(settings.get("string")));

        settings.put("longstring", LONG_STRING);
        results.assertTrue("Should be able to store and retrieve a string", LONG_STRING.equals(settings.get("longstring")));

        List list = Arrays.asList("foo");
        settings.put("list", list);
        list = (List) settings.get("list");
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
        } catch (IllegalArgumentException ex)
        {
            results.pass("Should not allow Map");
        }
    }
}