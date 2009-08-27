package com.atlassian.sal.fisheye.auth;

import java.net.URI;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;

import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.ApplicationProperties;

public class FisheyeLoginUriProvider implements LoginUriProvider
{
    private final ApplicationProperties applicationProperties;

    public FisheyeLoginUriProvider(ApplicationProperties applicationProperties)
    {
        this.applicationProperties = applicationProperties;
    }

    public URI getLoginUri(final URI returnUri)
    {
        try
        {
            return URI.create(
                    applicationProperties.getBaseUrl() +
                    "/login?origUrl=" +
                    (!returnUri.isAbsolute() ? applicationProperties.getBaseUrl() : "") +
                    URLEncoder.encode(returnUri.toString(),
                    "UTF-8"));
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException("Your JVM is broken", e);
        }
    }

}