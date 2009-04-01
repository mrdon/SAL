package com.atlassian.sal.api.pluginsettings;

/**
 * Provides access to settings globally or per project/space/repository/build-plan
 * <p/>
 * The following types are supported:
 * <ul>
 * <li>java.lang.String</li>
 * <li>java.util.List</li>
 * <li>java.util.Properties</li>
 * </ul>
 * <p/>
 * Instances are assumed to be not threadsafe and mutable.
 *
 * @since 2.0
 */
public interface PluginSettings
{
    /**
     * Gets a setting value
     *
     * @param key The setting key.  Cannot be null
     * @return The setting value
     */
    Object get(String key);

    /**
     * Puts a setting value.
     *
     * @param key   Setting key.  Cannot be null
     * @param value Setting value.  Can be null to remove.
     * @return The setting value that was removed.
     */
    Object put(String key, Object value);

    /**
     * Removes a setting value
     *
     * @param key The setting key
     * @return The setting value that was removed
     */
    Object remove(String key);
}
