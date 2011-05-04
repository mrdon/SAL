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
 * Implementations are only required to support writes of keys upto 255 characters in length
 * and are expected to throw an exception if the key is longer than 255 characters.
 * 
 * Reads and removes must support keys with a maximum length of 99000 character.
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
     *                                  or null, or if the key is null or longer than 255 characters
     */
    Object put(String key, Object value);

    /**
     * Removes a setting value
     *
     * @param key The setting key
     * @return The setting value that was removed. Null if nothing was removed.
     */
    Object remove(String key);
}
