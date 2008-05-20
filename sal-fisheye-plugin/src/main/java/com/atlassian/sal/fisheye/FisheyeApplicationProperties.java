package com.atlassian.sal.fisheye;

import com.atlassian.sal.api.ApplicationProperties;
import com.cenqua.fisheye.AppConfig;
import com.cenqua.fisheye.config.RootConfig;
import com.cenqua.fisheye.config1.WebServerType;

/**
 *
 */
public class FisheyeApplicationProperties implements ApplicationProperties
{
    public String getBaseUrl()
    {
        RootConfig getsConfig = AppConfig.getsConfig();
        String siteURL = getsConfig.getSiteURL();
        WebServerType ws = getsConfig.getConfig().getWebServer();
        return removeTrailingSlash(siteURL);
    }

    public String getApplicationName()
    {
        return "FishEye";
    }

    String removeTrailingSlash(String siteURL)
    {
        if (siteURL!=null && siteURL.endsWith("/"))
        {
            siteURL = siteURL.substring(0,siteURL.length()-1);
        }
        return siteURL;
    }
}
