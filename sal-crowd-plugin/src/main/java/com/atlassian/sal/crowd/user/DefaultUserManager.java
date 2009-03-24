package com.atlassian.sal.crowd.user;

import com.atlassian.crowd.service.UserService;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserResolutionException;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * Crowd implementation of the UserManager
 */
public class DefaultUserManager implements UserManager
{
    private final UserService userService;

    public DefaultUserManager(final UserService userService)
    {
        this.userService = userService;
    }

    public String getRemoteUsername()
    {
        return userService.getRemoteUsername();
    }

    public boolean isSystemAdmin(final String username)
    {
        return userService.isSystemAdmin(username);
    }

    public boolean isUserInGroup(final String username, final String group)
    {
        return userService.isUserInGroup(username, group);
    }

    public boolean authenticate(final String username, final String password)
    {
        return userService.authenticate(username, password);
    }

    public Principal resolve(final String username) throws UserResolutionException
    {
        try
        {
            return userService.resolve(username);

        }
        catch (org.springframework.dao.DataAccessException e)
        {
            throw new UserResolutionException("Failed to find user with name <" + username + ">", e);
        }
    }

    public String getRemoteUsername(final HttpServletRequest request)
    {
        // TODO Implement SAL-16
        return getRemoteUsername();
    }
}