package com.atlassian.sal.api;

import java.io.File;
import java.util.Date;

/**
 * Component for looking up application properties specific to their web interface
 *
 * @since 2.0
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
     * @return the displayable name of the application
     */
    String getDisplayName();

    /**
     * @return the version of the application
     */
    String getVersion();

    /**
     * @return the build date of the application
     */
    Date getBuildDate();

    /**
     * @return the build number of the application
     */
    String getBuildNumber();


    /**
     * @return the home directory of the application or null if none is defined
     */
    File getHomeDirectory();

}
