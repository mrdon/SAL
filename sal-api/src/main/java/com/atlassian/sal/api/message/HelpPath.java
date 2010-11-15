package com.atlassian.sal.api.message;

/**
 * Link to product specific help displayed in the UI.
 * @since 2.4
 */
public interface HelpPath
{
    /**
     * The key to the help item.
     * This method should never return null.  If there is no URL for a key or the key is null then the {@link HelpPathResolver}
     * should return null from the getHelpPath() method.
     * @return The key to the help item.
     */
    String getKey();

    /**
     * The full URL to the help content for this link.
     * This should never return null.   If there is no URL for a key then the {@link HelpPathResolver} should return null from
     * the getHelpPath() method.
     * @return The full URL to the help content for this link.
     */
    String getUrl();

    /**
     * Title attribute on the help link (the tooltip).
     * @return Title attribute on the help link or null if there is none provided.
     */
    String getTitle();

    /**
     * Alternate text for the help icon.
     * @return Alternate text for the help icon or null if there is none provided.
     */
    String getAlt();

    /**
     * Flag to indicate this is a local URL,
     * A local path is within the application context.
     * Clients may display local help links differently, especially for help that is generated dynamically.
     * @return true for local help links.
     */
    boolean isLocal();
}
