package com.atlassian.sal.fisheye.user;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserResolutionException;
import com.atlassian.sal.fisheye.appconfig.FisheyeUserManagerAccessor;
import com.cenqua.fisheye.rep.DbException;

/**
 * FishEye implementation of the UserManager
 */
public class DefaultUserManager implements UserManager
{
    private final FisheyeUserManagerAccessor fisheyeUserManagerAccessor;

    public DefaultUserManager(final FisheyeUserManagerAccessor fisheyeUserManagerAccessor)
    {
        this.fisheyeUserManagerAccessor = fisheyeUserManagerAccessor;
    }

    public String getRemoteUsername()
    {
        return fisheyeUserManagerAccessor.getRemoteUsername();
    }

    public boolean isSystemAdmin(final String username)
    {
        return fisheyeUserManagerAccessor.isSystemAdmin(username);
    }

    public boolean isUserInGroup(final String username, final String group)
    {
        return fisheyeUserManagerAccessor.isUserInGroup(username, group);
    }

    public boolean authenticate(final String username, final String password)
    {
        return fisheyeUserManagerAccessor.authenticate(username, password);
    }

    public String getRemoteUsername(final HttpServletRequest request)
    {
        return fisheyeUserManagerAccessor.getRemoteUsername(request);
    }

    public Principal resolve(final String username) throws UserResolutionException
    {
        try
        {
            fisheyeUserManagerAccessor.getUser(username);
        } catch (final DbException e)
        {
            throw new UserResolutionException("User '" + username + "' doesn't exist.", e);
        }
        return new Principal()
        {
            public String getName()
            {
                return username;
            }
        };
    }

}
