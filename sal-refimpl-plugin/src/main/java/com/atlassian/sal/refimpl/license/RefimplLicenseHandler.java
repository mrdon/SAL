package com.atlassian.sal.refimpl.license;

import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.sal.core.license.AbstractLicenseHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Simple implementation of license handler
 */
public class RefimplLicenseHandler extends AbstractLicenseHandler implements LicenseHandler
{
    private static final Log log = LogFactory.getLog(RefimplLicenseHandler.class);

    /**
     * Sets the license, going through the regular validation steps as if you used the web UI
     *
     * @param license The license string
     */
    public void setValidatedLicense(String license)
    {
        log.info("Setting license "+license);
    }
}
