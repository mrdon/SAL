package com.atlassian.sal.api.message;

/**
 * Link to product specific help displayed in the UI.
 * @since 2.4
 */
public interface HelpPath
{
    /**
     * Alternate text for the help icon.
     * @return Alternate text for the help icon.
     */
    String getAlt();

    /**
     * Title attribute on the help link (the tooltip).
     * @return Title attribute on the help link
     */
    String getTitle();

    /**
     * The key to the help item.
     * @return The key to the help item.
     */
    String getKey();

    /**
     * The full URL to the help content for this link.
     * @return The full URL to the help content for this link.
     */
    String getUrl();

    /**
     * Flag to indicate this is a local URL,
     * A local path is within the application context.
     * Clients may display local help links differently, especially for help that is generated dynamically.
     * @return true for local help links.
     */
    boolean isLocal();
}
