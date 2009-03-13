package com.atlassian.sal.confluence.auth;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.seraph.config.SecurityConfigFactory;

public class ConfluenceLoginUriProvider implements LoginUriProvider
{

    public URI getLoginUri(final URI returnUri)
    {
        final String linkLoginURL = SecurityConfigFactory.getInstance().getLinkLoginURL();
        try
        {
            final String newUrl = linkLoginURL.replace("${originalurl}", URLEncoder.encode(returnUri.toString(),"UTF-8"));
            return new URI(newUrl);
        } catch (final URISyntaxException e)
        {
            throw new RuntimeException("Error getting login uri. LoginUrl = "+linkLoginURL+", ReturnUri = " + returnUri, e);
        } catch (final UnsupportedEncodingException e)
        {
            throw new RuntimeException("Error getting login uri. LoginUrl = "+linkLoginURL+", ReturnUri = " + returnUri, e);
        }
    }

}
