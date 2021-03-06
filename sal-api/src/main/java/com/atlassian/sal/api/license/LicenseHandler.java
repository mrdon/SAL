package com.atlassian.sal.api.license;

/**
 * Interface into the license system for the individual application
 *
 * @since 2.0
 */
public interface LicenseHandler
{
    /**
     * Sets the license string for the currently running application
     *
     * @param license The license string
     * @throws IllegalArgumentException if the license string is not a valid license
     */
    void setLicense(String license);

    /**
     * Gets the server ID of the currently running application.  The server ID format is four quadruples of
     * alphanumeric characters, each separated by a dash (<tt>-</tt>).
     *
     * @return the server ID, or {@code null} if the server ID has not yet
     *         been set for the currently running application.
     *
     * @since 2.7
     */
    String getServerId();

    /**
     * Gets the Support Entitlement Number (SEN) for the currently running application.
     *
     * @return the Support Entitlement Number, or {@code null} if there is no current support entitlement.
     *
     * @since 2.7
     */
    String getSupportEntitlementNumber();
}
