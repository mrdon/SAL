package com.atlassian.sal.fisheye.auth;

import java.net.URI;

import com.atlassian.sal.api.auth.LoginUriProvider;

public class FisheyeLoginUriProvider implements LoginUriProvider
{

    public URI getLoginUri(final URI returnUri)
    {
        throw new UnsupportedOperationException("LoginUriProvider in FishEye is not implemented yet");
    }

}