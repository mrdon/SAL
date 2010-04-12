package com.atlassian.sal.api.license;

/**
 * Interface into the license system for the individual application
 *
 * @since 2.0
 */
public interface LicenseHandler
{
    /**
     * @return  the Atlassian server id of the application, or {@code null} if
     * the server id is not set.
     */
    String getServerId();

    /**
     * Sets the license string for the currently running application
     *
     * @param license The license string
     * @throws IllegalArgumentException if the license string is not a valid license
     */
    void setLicense(String license);
}
