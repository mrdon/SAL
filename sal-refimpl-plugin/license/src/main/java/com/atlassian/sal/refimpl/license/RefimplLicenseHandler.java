package com.atlassian.sal.refimpl.license;

import com.atlassian.sal.api.license.LicenseHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.UUID;

/**
 * Simple implementation of license handler
 */
public class RefimplLicenseHandler implements LicenseHandler
{
    private static final Log log = LogFactory.getLog(RefimplLicenseHandler.class);
    private static final String SERVER_ID = UUID.randomUUID().toString();

    public String getServerId()
    {
        return SERVER_ID;
    }

    /**
     * Sets the license, going through the regular validation steps as if you used the web UI
     *
     * @param license The license string
     */
    public void setLicense(String license)
    {
        log.info("Setting license "+license);
    }
}
