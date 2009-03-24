package com.atlassian.sal.fisheye.auth;

import com.atlassian.sal.api.auth.AuthenticationController;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * Fisheye authentication controller
 *
 * @since 2.0.0
 */
public class FisheyeAuthenticationController implements AuthenticationController
{
    public boolean shouldAttemptAuthentication(HttpServletRequest request)
    {
        throw new UnsupportedOperationException();
    }

    public boolean canLogin(Principal principal, HttpServletRequest request)
    {
        throw new UnsupportedOperationException();
    }
}
