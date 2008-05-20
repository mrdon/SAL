package com.atlassian.sal.api.http;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class for storing username and password data for use by subclasses, which usually use this information to
 * authenticate a connection. An Authenticator typically doesn't do any actual authentication, instead it stores
 * information used in authentication.
 */
public abstract class Authenticator implements Serializable
{

    /**
     * Repository for those properties which have been stored in this <code>Authenticator</code>
     */
    private final Map<String, String> properties = new HashMap<String, String>();

    /**
     * The allowed property keys which can be stored in and retrieved from an <code>Authenticator</code>
     */
    private static final String[] SIMPLE_PROPERTIES = { "username", "password" };

    /**
     * Retrieves the keys which can be used to store properties in an <code>Authenticator</code>. If an attempt is made
     * to use a string which is not in this array, an <code>IllegalArgumentException</code> will be thrown.
     *
     * @return an array containing all of, and only, those strings which are valid keys to be stored in an <code>Authenticator</code>
     */
    public static String[] getPropertyNames()
    {
        // make a new array so that the internals can't be changed.
        return new String[]{ SIMPLE_PROPERTIES[0], SIMPLE_PROPERTIES[1] };
    }

    /**
     * Sets a property; currently either the user's name or the user's password
     *
     * @param key the key by whihc this property is stored, it must be from the array given by <code>getPropertyNames</code>
     * @param value the value which is to be stored
     */
    public final void setProperty(final String key, final String value)
    {

        for (String propertyName : getPropertyNames())
        {
            if (propertyName.equals(key))
            {
                this.properties.put(key, value);
                return;
            }
        }

        throw new IllegalArgumentException("Unknown property: " + key);
    }

    /**
     * Retrieves a previously stored property; either the user name or password
     *
     * @param key the property to be retrieved
     * @return the stored value of the property, returns null if the property is invalid or has not been stored yet
     */
    protected String getProperty(final String key)
    {
        return properties.get(key);
    }

}
