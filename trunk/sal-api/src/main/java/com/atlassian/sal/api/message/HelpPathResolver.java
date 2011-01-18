package com.atlassian.sal.api.message;

/**
 * Help path resolver.
 * This is used to retrieve product specific help.
 * Plugins used with multiple Atlassian products may wish to have help integrated with the core product help,
 * e.g. The administration plugin for embedded crowd directory management has different facilities in JIRA and Confluence
 * and both JIRA and Confluence supply their own help pages for the plugin.
 *
 * @since 2.4
 */
public interface HelpPathResolver
{
    /**
     * Retrieve the help path for a key.
     * Implementers may choose to return null if no help is available for the given key.
     * @param key Key to the help path.  The key passed in may be null.
     * @return HelpPath for the key.
     */
    HelpPath getHelpPath(String key);
}
