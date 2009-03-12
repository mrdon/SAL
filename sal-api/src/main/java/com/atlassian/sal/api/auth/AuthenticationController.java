package com.atlassian.sal.api.auth;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

/**
 * Allows the host application to communicate to the OAuth system details about when authentication should be performed
 * and users allowed to login.
 */
public interface AuthenticationController
{
    /**
     * Check whether or not authentication via OAuth should be tried. Typically this will return
     * {@code true} if the current principal is not already authenticated.
     *
     * @param request the current {@link HttpServletRequest}
     * @return {@code true} if OAuth authentication should be tried, {@code false} otherwise.
     */
    boolean shouldAttemptAuthentication(HttpServletRequest request);

    /**
     * Check whether the given principal can log into the application for the current request.
     *
     * @param principal the identified principal
     * @param request the current {@link HttpServletRequest}
     * @return {@code true} if the principal is allowed to login for the given request, {@code false}
     * otherwise.
     */
    boolean canLogin(Principal principal, HttpServletRequest request);
}
