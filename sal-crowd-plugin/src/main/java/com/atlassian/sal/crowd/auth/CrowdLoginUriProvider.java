package com.atlassian.sal.crowd.auth;

import java.net.URI;

import com.atlassian.sal.api.auth.LoginUriProvider;

public class CrowdLoginUriProvider implements LoginUriProvider
{

    public URI getLoginUri(final URI returnUri)
    {
        throw new UnsupportedOperationException("LoginUriProvider in Crowd is not implemented yet");
    }

}
