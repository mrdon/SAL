package com.atlassian.sal.fisheye.license;

import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.sal.core.license.AbstractLicenseHandler;
import com.cenqua.fisheye.AppConfig;
import com.cenqua.fisheye.license.LicenseException;
import com.cenqua.fisheye.config.RootConfig;
import com.cenqua.fisheye.config1.LicenseType;

import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * License handler for fisheye
 */
public class FisheyeLicenseHandler extends AbstractLicenseHandler implements LicenseHandler
{
    private static Logger log = Logger.getLogger(FisheyeLicenseHandler.class);

    protected void setValidatedLicense(String license)
    {
        RootConfig rootConfig = AppConfig.getsConfig();
        LicenseType licenses = AppConfig.getsConfig().getConfig().getLicense();
        licenses.setCrucible(license);
        licenses.setFisheye(license);
        try
        {
            rootConfig.saveConfig();
            rootConfig.refreshLicenses();
        }
        catch (IOException ioe)
        {
            log.error("Error saving configuration while reloading license", ioe);
        }
        catch (LicenseException le)
        {
            log.error("Error loading license", le);
        }
    }
}
