package com.atlassian.sal.core.license;

import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.license.LicensePair;
import com.atlassian.license.LicenseException;

/**
 * Base license handler that does basic validation
 */
public abstract class AbstractLicenseHandler implements LicenseHandler
{
    public void setLicense(String license)
    {
        try
        {
            new LicensePair(license);
        }
        catch (LicenseException e)
        {
            //log.warn("The license you specified was invalid.");
            //addFieldError("licenseString", getText("license.invalid.error", new Object[] {e.getMessage()}));
            throw new IllegalArgumentException("Invalid license format", e);
        }
        setValidatedLicense(license);
    }

    protected abstract void setValidatedLicense(String license);
}
