package com.atlassian.sal.api.pluginsettings;

/**
 * Factory for mutable, non-threadsafe PluginSettings objects.
 */
public interface PluginSettingsFactory
{
    /**
     * Gets all settings for a key, usually a space, project, or repository key
     * @param key The key, can be null to retrieve global settings
     * @return The settings
     */
    PluginSettings createSettingsForKey(String key);

    /**
     * Gets all global settings
     * @return Global settings
     */
    PluginSettings createGlobalSettings();
    
    /**
     * Gets settings for given user
     * @param username
     * @return
     */
    PluginSettings createUserSettings(String username);
    
}
