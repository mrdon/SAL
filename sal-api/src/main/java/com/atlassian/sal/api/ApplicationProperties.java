package com.atlassian.sal.api;

import java.util.Date;
import java.util.Set;
import java.io.File;

/**
 * Component for looking up application properties specific to their web interface
 */
public interface ApplicationProperties
{
    /**
     * Get the base URL of the current application.
     * @return the current application's base URL
     */
    String getBaseUrl();

    /**
     * @return the displayable name of the application
     * @since 2.0.0
     */
    String getDisplayName();

    /**
     * Returns the version of the application
     * @since 2.0.0
     */
    String getVersion();

    /**
     * Returns the build date of the application
     * @since 2.0.0
     */
    Date getBuildDate();

    /**
     * Returns the build number of the application
     * @since 2.0.0
     */
    String getBuildNumber();


    /**
     * @return the home directory of the application or null if none is defined
     * @since 2.2.0
     */
    File getHomeDirectory();

}
