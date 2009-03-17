package com.atlassian.sal.crowd.user;

import com.atlassian.crowd.service.UserService;
import com.atlassian.sal.api.user.UserManager;

import javax.servlet.http.HttpServletRequest;

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

    public String getRemoteUsername(final HttpServletRequest request)
    {
        // TODO Implement SAL-16
        return getRemoteUsername();
    }
}

