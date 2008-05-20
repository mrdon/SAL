package com.atlassian.sal.api;

/**
 * Component for looking up application properties specific to their web interface
 */
public interface ApplicationProperties
{
    /**
     * Get the base URL of the current application.
     *
     * @return the current application's base URL
     */
    String getBaseUrl();

    /**
     * Returns one of JIRA, Confluence or FishEye
     * @return one of JIRA, Confluence or FishEye
     */
    String getApplicationName();
}
