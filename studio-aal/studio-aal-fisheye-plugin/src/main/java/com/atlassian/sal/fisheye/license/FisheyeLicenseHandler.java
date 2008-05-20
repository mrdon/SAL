package com.atlassian.sal.fisheye.license;

import com.atlassian.sal.api.license.LicenseHandler;
import com.cenqua.fisheye.AppConfig;
import com.cenqua.fisheye.config1.LicenseType;

/**
 * License handler for fisheye
 */
public class FisheyeLicenseHandler implements LicenseHandler
{
    public void setLicense(String license)
    {
        LicenseType licenses = AppConfig.getsConfig().getConfig().getLicense();
        licenses.setCrucible(license);
        licenses.setFisheye(license);
    }
}
