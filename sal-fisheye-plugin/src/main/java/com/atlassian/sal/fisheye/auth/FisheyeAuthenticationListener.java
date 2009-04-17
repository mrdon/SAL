package com.atlassian.sal.fisheye.auth;

import com.atlassian.sal.api.auth.AuthenticationListener;
import com.atlassian.sal.api.auth.Authenticator;
import com.atlassian.sal.fisheye.appconfig.FisheyeUserManagerAccessor;
import com.cenqua.fisheye.user.UserManager;
import com.cenqua.fisheye.user.UserLogin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Fisheye authentication listener
 *
 * @since 2.0.0
 */
public class FisheyeAuthenticationListener implements AuthenticationListener
{
    private final FisheyeUserManagerAccessor uma;

    public FisheyeAuthenticationListener(FisheyeUserManagerAccessor uma)
    {
        this.uma = uma;
    }

    public void authenticationSuccess(Authenticator.Result result, HttpServletRequest request, HttpServletResponse response)
    {
        String name = result.getPrincipal().getName();
        uma.loginUserForThisRequest(name, request);
    }

    public void authenticationFailure(Authenticator.Result result, HttpServletRequest request, HttpServletResponse response)
    {
    }

    public void authenticationError(Authenticator.Result result, HttpServletRequest request, HttpServletResponse response)
    {
    }

    public void authenticationNotAttempted(HttpServletRequest request, HttpServletResponse response)
    {
    }
}
