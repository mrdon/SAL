package com.atlassian.sal.fisheye;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.File;

import org.apache.commons.lang.StringUtils;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.fisheye.appconfig.FisheyeAccessor;
import com.cenqua.fisheye.FisheyeVersionInfo;

/**
 *
 */
public class FisheyeApplicationProperties implements ApplicationProperties
{

    private final FisheyeAccessor fisheyeAccessor;
    public FisheyeApplicationProperties(final FisheyeAccessor fisheyeAccessor)
    {
        this.fisheyeAccessor = fisheyeAccessor;
    }

    public String getBaseUrl()
    {
        final String siteURL = fisheyeAccessor.getSiteURL();
        return StringUtils.removeEnd(siteURL, "/");
    }

    public String getDisplayName()
    {
        return "FishEye";
    }

    public String getVersion()
    {
        return FisheyeVersionInfo.RELEASE_NUM;
    }

    public Date getBuildDate()
    {
        final String buildDateFormat = "yyyy-MM-dd";
        try
        {
            return new SimpleDateFormat(buildDateFormat).parse(FisheyeVersionInfo.BUILD_DATE);
        }
        catch (final ParseException e)
        {
            throw new RuntimeException("Unable to parse FishEye build date <" + FisheyeVersionInfo.BUILD_DATE + "> into format " + buildDateFormat, e);
        }
    }

    public String getBuildNumber()
    {
        return FisheyeVersionInfo.BUILD_NUMBER;
    }

    /**
     * @return the FishEye instance directory, not the home or application directory
     */
    public File getHomeDirectory()
    {
        return fisheyeAccessor.getInstanceDirectory();
    }
}
