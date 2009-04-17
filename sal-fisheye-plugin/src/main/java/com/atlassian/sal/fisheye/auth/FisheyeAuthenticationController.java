package com.atlassian.sal.fisheye.auth;

import com.atlassian.sal.api.auth.AuthenticationController;
import com.atlassian.sal.fisheye.appconfig.FisheyeUserManagerAccessor;
import com.cenqua.fisheye.user.FEUser;
import com.cenqua.fisheye.rep.DbException;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

import org.apache.log4j.Logger;

/**
 * Fisheye authentication controller
 *
 * @since 2.0.0
 */
public class FisheyeAuthenticationController implements AuthenticationController
{
    private static final Logger LOG = Logger.getLogger(FisheyeAuthenticationController.class);

    private final FisheyeUserManagerAccessor uma;

    public FisheyeAuthenticationController(FisheyeUserManagerAccessor uma)
    {
        this.uma = uma;
    }

    public boolean shouldAttemptAuthentication(HttpServletRequest request)
    {
        return uma.getRemoteUsername(request) == null;
    }

    public boolean canLogin(Principal principal, HttpServletRequest request)
    {
        if (principal == null)
        {
            return false;
        }

        try {
            FEUser user = uma.getUser(principal.getName());
            return user != null;
        } catch (DbException e) {
            LOG.error("Could not check user", e);
            return false;
        }
    }
}
