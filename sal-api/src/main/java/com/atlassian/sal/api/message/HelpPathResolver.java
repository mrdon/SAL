package com.atlassian.sal.api.message;

/**
 * Help path resolver.
 * This is used to retrieve product specific help.
 *
 * @since 2.4
 */
public interface HelpPathResolver
{
    /**
     * Retrieve the help path for a key.
     * Implementers may choose to return null if no help is available, or if they prefer they can choose to
     * return a default value.
     * @param key Key to the help path.
     * @return HelpPath for the key.
     */
    HelpPath getHelpPath(String key);
}
