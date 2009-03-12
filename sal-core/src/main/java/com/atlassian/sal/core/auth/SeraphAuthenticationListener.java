package com.atlassian.sal.core.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atlassian.oauth.Authenticator;
import com.atlassian.oauth.spi.AuthenticationListener;
import com.atlassian.seraph.auth.DefaultAuthenticator;
import com.atlassian.seraph.filter.BaseLoginFilter;

public class SeraphAuthenticationListener implements AuthenticationListener
{
    public void authenticationSuccess(Authenticator.Result result, HttpServletRequest request, HttpServletResponse response)
    {
        request.getSession().setAttribute(DefaultAuthenticator.LOGGED_IN_KEY, result.getUser());
        request.getSession().setAttribute(DefaultAuthenticator.LOGGED_OUT_KEY, null);
        request.setAttribute(BaseLoginFilter.OS_AUTHSTATUS_KEY, BaseLoginFilter.LOGIN_SUCCESS);
    }

    public void authenticationError(Authenticator.Result result, HttpServletRequest request, HttpServletResponse response)
    {
    }

    public void authenticationFailure(Authenticator.Result result, HttpServletRequest request, HttpServletResponse response)
    {
    }

    public void authenticationNotAttempted(HttpServletRequest request, HttpServletResponse response)
    {
    }
}
