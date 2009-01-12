package com.atlassian.sal.rest;

import com.atlassian.sal.api.ApplicationProperties;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement(name = "application")
public class RestApplicationProperties implements ApplicationProperties
{
    @XmlElement
    private String baseUrl;

    @XmlElement
    private String applicationName;

    @XmlElement
    private String version;

    @XmlElement
    private Date buildDate;

    @XmlElement
    private String buildNumber;

    RestApplicationProperties()
    {
    }

    public RestApplicationProperties(ApplicationProperties applicationProperties)
    {
        baseUrl = applicationProperties.getBaseUrl();
        applicationName = applicationProperties.getApplicationName();
        version = applicationProperties.getVersion();
        buildDate = applicationProperties.getBuildDate();
        buildNumber = applicationProperties.getBuildNumber();
    }

    public String getBaseUrl()
    {
        return baseUrl;
    }

    public String getApplicationName()
    {
        return applicationName;
    }

    public String getVersion()
    {
        return version;
    }

    public Date getBuildDate()
    {
        return buildDate;
    }

    public String getBuildNumber()
    {
        return buildNumber;
    }

}