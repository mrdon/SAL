package com.atlassian.sal.crowd;

import com.atlassian.sal.api.license.LicenseHandler;

public class CrowdLicenseHandler implements LicenseHandler
{

    public void setLicense(final String license)
    {
        throw new UnsupportedOperationException("LicenseHandler is Crowd is not implemented.");
    }

}
