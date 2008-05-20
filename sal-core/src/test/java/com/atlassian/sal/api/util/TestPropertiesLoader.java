package com.atlassian.sal.api.util;

import junit.framework.TestCase;

import java.util.Properties;

import com.atlassian.sal.api.util.PropertiesLoader;

/**
 *
 */
public class TestPropertiesLoader extends TestCase
{

    public void testLoadSampleProperties()
    {
        final Properties properties = PropertiesLoader.loadProperties("sample.properties");
        assertNotNull(properties);
        assertEquals("foobar", properties.getProperty("sample.key"));
    }

    public void testLoadSamplePropertiesUsingInvalidLocation()
    {
        System.setProperty("sample.properties.location", "/some/invalid/path");

        //this should work anyways, since we'll fall back to the classpath loader
        final Properties properties = PropertiesLoader.loadProperties("sample.properties");
        assertNotNull(properties);
        assertEquals("foobar", properties.getProperty("sample.key"));
    }

    public void testLoadInvalidProperties()
    {
        final Properties properties = PropertiesLoader.loadProperties("dontexist.properties");
        assertNotNull(properties);
        assertTrue(properties.keySet().isEmpty());
    }
}
