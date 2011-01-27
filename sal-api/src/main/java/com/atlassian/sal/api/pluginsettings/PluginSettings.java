package com.atlassian.sal.api.pluginsettings;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Provides access to settings globally or per project/space/repository/build-plan
 * <p/>
 * The following types are supported:
 * <ul>
 * <li>java.lang.String</li>
 * <li>java.util.List</li>
 * <li>java.util.Properties</li>
 * <li>java.util.Map</li>
 * </ul>
 * {@link List} and {@link Map} types must contain only {@link String}.
 * <p/>
 * The maximum length of a key is 100 characters.
 * <p/>
 * Instances are assumed to be not threadsafe and mutable.
 *
 * @since 2.0
 */
public interface PluginSettings
{
    /**
     * Gets a setting value. The setting returned should be specific to this context settings object and not cascade
     * the value to a global context.
     *
     * @param key The setting key.  Cannot be null
     * @return The setting value. May be null
     * @throws IllegalArgumentException if the key is null or longer than 100 characters
     */
    Object get(String key);

    /**
     * Puts a setting value.
     *
     * @param key   Setting key.  Cannot be null
     * @param value Setting value.  Must be one of {@link String}, {@link List}, {@link Properties}, {@link Map}, or null.
     *              null will remove the item from the settings.
     * @return The setting value that was over ridden. Null if none existed.
     * @throws IllegalArgumentException if value is not {@link String}, {@link List}, {@link Properties}, {@link Map},
     *                                  or null, or if the key is null or longer than 100 characters
     */
    Object put(String key, Object value);

    /**
     * Removes a setting value
     *
     * @param key The setting key
     * @return The setting value that was removed. Null if nothing was removed.
     * @throws IllegalArgumentException if the key is null or longer than 100 characters
     */
    Object remove(String key);
}
