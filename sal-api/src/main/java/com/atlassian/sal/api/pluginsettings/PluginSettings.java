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
 * Implementations are only required to support writes of keys upto 100 characters in length
 * and are expected to throw an exception if the key is longer than 255 characters.  
 * Keys should be kept as short as possible.
 * 
 * Reads and removes must support keys longer than 256 characters.
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
     * @throws IllegalArgumentException if the key is null.
     */
    Object get(String key);

    /**
     * Puts a setting value.
     *
     * @param key   Setting key.  Cannot be null, keys longer than 100 characters are not supported.
     * @param value Setting value.  Must be one of {@link String}, {@link List}, {@link Properties}, {@link Map}, or null.
     *              null will remove the item from the settings.  If the value is a {@link String} it should not be longer 
     *              than 99000 characters long.  Values of a type other than {@link String} will be serialized as a 
     *              {@link String} which cannot be longer than 99000 characters long.
     * @return The setting value that was over ridden. Null if none existed.
     * @throws IllegalArgumentException if value is not null, {@link String}, {@link List}, {@link Properties} or {@link Map},
     *              or if the key is null or longer than 255 characters
     */
    Object put(String key, Object value);

    /**
     * Removes a setting value
     *
     * @param key The setting key
     * @return The setting value that was removed. Null if nothing was removed.
     * @throws IllegalArgumentException if the key is null.
     */
    Object remove(String key);
}
