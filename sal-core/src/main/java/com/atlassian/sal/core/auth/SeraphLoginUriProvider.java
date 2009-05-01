package com.atlassian.sal.core.auth;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.seraph.config.SecurityConfigFactory;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

public class SeraphLoginUriProvider implements LoginUriProvider
{
    private final ApplicationProperties applicationProperties;

    public SeraphLoginUriProvider(ApplicationProperties applicationProperties)
    {
        this.applicationProperties = applicationProperties;
    }

    public URI getLoginUri(final URI returnUri)
    {
        final String loginURL = SecurityConfigFactory.getInstance().getLoginURL();
        try
        {
            final String newUrl = loginURL.replace("${originalurl}", URLEncoder.encode(returnUri.toString(), "UTF-8"));
            return new URI(applicationProperties.getBaseUrl() + newUrl);
        }
        catch (final URISyntaxException e)
        {
            throw new RuntimeException("Error getting login uri. LoginUrl = " + loginURL + ", ReturnUri = " + returnUri, e);
        }
        catch (final UnsupportedEncodingException e)
        {
            throw new RuntimeException("Error getting login uri. LoginUrl = " + loginURL + ", ReturnUri = " + returnUri, e);
        }
    }
}
