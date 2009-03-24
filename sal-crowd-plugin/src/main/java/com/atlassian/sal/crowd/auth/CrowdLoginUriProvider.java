package com.atlassian.sal.crowd.auth;

import java.net.URI;

import com.atlassian.sal.api.auth.LoginUriProvider;

public class CrowdLoginUriProvider implements LoginUriProvider
{
//    private final ClientProperties clientProperties;
//
//    public CrowdLoginUriProvider(final ClientProperties clientProperties)
//    {
//        this.clientProperties = clientProperties;
//    }

    public URI getLoginUri(final URI returnUri)
    {
//        clientProperties.getApplicationAuthenticationURL();
        throw new UnsupportedOperationException("LoginUriProvider in Crowd is not implemented yet");
    }

}
