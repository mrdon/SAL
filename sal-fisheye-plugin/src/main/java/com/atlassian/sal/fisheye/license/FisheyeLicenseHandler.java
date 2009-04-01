package com.atlassian.sal.fisheye.license;

import org.apache.log4j.Logger;

import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.sal.fisheye.appconfig.FisheyeAccessor;
import com.atlassian.sal.fisheye.appconfig.FisheyeAccessor.FisheyeAccessorException;

/**
 * License handler for fisheye
 */
public class FisheyeLicenseHandler implements LicenseHandler
{
    private static Logger log = Logger.getLogger(FisheyeLicenseHandler.class);
    private final FisheyeAccessor fisheyeAccessor;

    public FisheyeLicenseHandler(final FisheyeAccessor fisheyeAccessor)
    {
        this.fisheyeAccessor = fisheyeAccessor;
    }

    public void setLicense(final String license)
    {
        try
        {
            fisheyeAccessor.setLicense(license);
        } catch (final FisheyeAccessorException e)
        {
            log.error(e,e);
            throw new IllegalArgumentException("Specified license was invalid."); // @TODO Need to check if this is true or not
        }
    }
}
