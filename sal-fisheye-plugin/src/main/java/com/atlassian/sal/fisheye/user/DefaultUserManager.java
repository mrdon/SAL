package com.atlassian.sal.fisheye.user;

import javax.servlet.http.HttpServletRequest;

import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.fisheye.appconfig.FisheyeUserManagerAccessor;

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

}
