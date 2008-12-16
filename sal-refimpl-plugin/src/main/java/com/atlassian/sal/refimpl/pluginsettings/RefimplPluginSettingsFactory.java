package com.atlassian.sal.refimpl.pluginsettings;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Properties;
import java.util.AbstractMap;
import java.util.Set;
import java.util.Collections;

/**
 * This implementation can be backed by a file on the file system.  If a file in the current working directory called
 * "pluginsettings.xml" exists (can be overridden with system property sal.pluginsettings.store) exists, it loads and
 * persists all plugin settings to and from this file.  If no file exists, plugin settings are purely in memory.
 */
public class RefimplPluginSettingsFactory implements PluginSettingsFactory
{
    private static final Logger log = Logger.getLogger(RefimplPluginSettingsFactory.class);
    private final Properties properties;
    private final File file;

    public RefimplPluginSettingsFactory()
    {
        File file = new File(System.getProperty("sal.pluginsettings.store", "pluginsettings.xml"));
        properties = new Properties();
        if (file.exists() && file.canRead())
        {
            if (file.length() > 0)
            {
                InputStream is = null;
                try
                {
                    is = new FileInputStream(file);
                    properties.loadFromXML(is);
                    log.info("Using " + file.getAbsolutePath() + " as plugin settings store");
                }
                catch (Exception e)
                {
                    log.error("Error loading plugin settings properties", e);
                    file = null;
                }
                finally
                {
                    if (is != null)
                    {
                        try
                        {
                            is.close();
                        }
                        catch (IOException ioe)
                        {
                            log.error("Error closing file", ioe);
                        }
                    }
                }
            }
            else
            {
                // File is a new file
                log.info("Using " + file.getAbsolutePath() + " as plugin settings store");
            }
        }
        else
        {
            log.info("Cannot find property settings file, using memory store");
            file = null;
        }
        this.file = file;
    }

    public PluginSettings createSettingsForKey(String key)
    {
        return new RefimplPluginSettings(new SettingsMap(key));
    }

    public PluginSettings createGlobalSettings()
    {
        return createSettingsForKey(null);
    }

    @SuppressWarnings("AccessToStaticFieldLockedOnInstance")
    private synchronized void store()
    {
        if (file == null || !file.canWrite())
        {
            // Read only settings
            return;
        }
        OutputStream os = null;
        try
        {
            os = new FileOutputStream(file);
            properties.storeToXML(os, "SAL Reference Implementation plugin settings");
        }
        catch (IOException ioe)
        {
            log.error("Error storing properties", ioe);
        }
        finally
        {
            if (os != null)
            {
                try
                {
                    os.close();
                }
                catch (IOException ioe)
                {
                    log.error("Error closing output stream", ioe);
                }
            }
        }
    }

    private class SettingsMap extends AbstractMap<String, String>
    {
        private final String settingsKey;

        private SettingsMap(String settingsKey)
        {
            if (settingsKey == null)
            {
                this.settingsKey = "global.";
            }
            else
            {
                this.settingsKey = "keys." + settingsKey + ".";
            }
        }

        public Set<Entry<String, String>> entrySet()
        {
            // Not used
            return Collections.emptySet();
        }

        public String get(Object key)
        {
            return properties.getProperty(settingsKey + key);
        }

        public String put(String key, String value)
        {
            String result = (String) properties.setProperty(settingsKey + key, value);
            store();
            return result;
        }

        public String remove(Object key)
        {
            String result = (String) properties.remove(settingsKey + key);
            store();
            return result;
        }
    }
}
