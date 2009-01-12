package com.atlassian.sal.fisheye;

import com.atlassian.sal.api.ApplicationProperties;
import com.cenqua.fisheye.AppConfig;
import com.cenqua.fisheye.FisheyeVersionInfo;
import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 */
public class FisheyeApplicationProperties implements ApplicationProperties
{
    public String getBaseUrl()
    {
        final String siteURL = AppConfig.getsConfig().getSiteURL();
        return StringUtils.removeEnd(siteURL, "/");
    }

    public String getApplicationName()
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
        catch (ParseException e)
        {
            throw new RuntimeException("Unable to parse FishEye build date <" + FisheyeVersionInfo.BUILD_DATE + "> into format " + buildDateFormat, e);
        }
    }

    public String getBuildNumber()
    {
        return FisheyeVersionInfo.BUILD_NUMBER;
    }
}
