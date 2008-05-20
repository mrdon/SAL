package com.atlassian.sal.api.util;

import com.atlassian.sal.api.logging.Logger;
import com.atlassian.sal.api.logging.LoggerFactory;

import org.apache.commons.lang.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * Utility class responsible for loading {@link java.util.Properties} from the classpath or a location
 * specified by a startup variable.
 */
public class PropertiesLoader
{
    private static final Logger log = LoggerFactory.getLogger(PropertiesLoader.class);


    /**
     * Tries to load properties either from a location provided via a -DpropertiesName.location=/blah/blah.properties
     * paramenter or from the classpath.
     *
     * @param propertiesName The name of the properties to load.  E.g. studio.properites
     * @return Loaded properties.
     */
    public static Properties loadProperties(String propertiesName)
    {
        final String propertiesKey = propertiesName + ".location";
        final String propertiesLocation = System.getProperty(propertiesKey);
        final Properties properties = new Properties();
        if (StringUtils.isNotEmpty(propertiesLocation))
        {
            //try to load the file from the user supplied location.
            try
            {
                properties.load(new FileInputStream(propertiesLocation));
            }
            catch (IOException e)
            {
                log.error("Error reading " + propertiesName + " from location '" + propertiesLocation +
                          "'. Trying to load from classpath instead...", e);
                loadPropertiesFromClasspath(propertiesName, properties);
            }
        }
        else
        {
            loadPropertiesFromClasspath(propertiesName, properties);
        }

        return properties;
    }

    private static void loadPropertiesFromClasspath(String propertiesName, Properties properties)
    {
        try
        {
            properties.load(getResourceAsStream(propertiesName, PropertiesLoader.class));
        }
        catch (Exception e)
        {
            log.error("Error loading " + propertiesName + " properties from classpath.", e);
        }
    }

    /**
     * This is a convenience method to load a resource as a stream.
     * <p/>
     * The algorithm used to find the resource is given in getResource()
     *
     * @param resourceName The name of the resource to load
     * @param callingClass The Class object of the calling object
     */
    private static InputStream getResourceAsStream(String resourceName, Class callingClass)
    {
        URL url = getResource(resourceName, callingClass);
        try
        {
            return url != null ? url.openStream() : null;
        }
        catch (IOException e)
        {
            return null;
        }
    }

    /**
     * Load a given resource.
     * <p/>
     * This method will try to load the resource using the following methods (in order):
     * <ul>
     * <li>From {@link Thread#getContextClassLoader() Thread.currentThread().getContextClassLoader()}
     * <li>From {@link Class#getClassLoader() ClassLoaderUtil.class.getClassLoader()}
     * <li>From the {@link Class#getClassLoader() callingClass.getClassLoader() }
     * </ul>
     *
     * @param resourceName The name of the resource to load
     * @param callingClass The Class object of the calling object
     */
    private static URL getResource(String resourceName, Class callingClass)
    {
        URL url = null;

        url = Thread.currentThread().getContextClassLoader().getResource(resourceName);

        if (url == null)
        {
            url = PropertiesLoader.class.getClassLoader().getResource(resourceName);
        }

        if (url == null)
        {
            url = callingClass.getClassLoader().getResource(resourceName);
        }
        return url;
    }

}
