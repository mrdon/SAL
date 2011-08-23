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
     * Gets the server ID of the currently running application.
     *
     * @return the server ID, or {@code null} if a license is not yet applied 
     *         to the currently running application.
     */
    String getServerId();

    /**
     * Gets the Support Entitlement Number (SEN) for the currently running application.
     *
     * @return the Support Entitlement Number, or {@code null} if there is no current support entitlement.
     */
    String getSupportEntitlementNumber();
}
