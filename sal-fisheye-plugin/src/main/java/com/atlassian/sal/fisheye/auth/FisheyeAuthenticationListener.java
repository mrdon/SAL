package com.atlassian.sal.fisheye.auth;

import com.atlassian.sal.api.auth.AuthenticationListener;
import com.atlassian.sal.api.auth.Authenticator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Fisheye authentication listener
 *
 * @since 2.0.0
 */
public class FisheyeAuthenticationListener implements AuthenticationListener
{
    public void authenticationSuccess(Authenticator.Result result, HttpServletRequest request, HttpServletResponse response)
    {
        throw new UnsupportedOperationException();
    }

    public void authenticationFailure(Authenticator.Result result, HttpServletRequest request, HttpServletResponse response)
    {
        throw new UnsupportedOperationException();
    }

    public void authenticationError(Authenticator.Result result, HttpServletRequest request, HttpServletResponse response)
    {
        throw new UnsupportedOperationException();
    }

    public void authenticationNotAttempted(HttpServletRequest request, HttpServletResponse response)
    {
        throw new UnsupportedOperationException();
    }
}
