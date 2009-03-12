package com.atlassian.sal.confluence.user;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserResolutionException;
import com.atlassian.user.User;

/** Authenticates a user against UserAccessor in Confluence. */
public class DefaultUserManager implements UserManager
{
    private final UserAccessor userAccessor;

    public DefaultUserManager(final UserAccessor userAccessor)
    {
        this.userAccessor = userAccessor;
    }

    public String getRemoteUsername()
    {
        final User user = AuthenticatedUserThreadLocal.getUser();
        if (user != null)
        {
            return user.getName();
        }
        return null;
    }

    public boolean isSystemAdmin(final String username)
    {
        final User user = userAccessor.getUser(username);
        return user != null && userAccessor.isSuperUser(user);
    }

    public boolean authenticate(final String username, final String password)
    {
        final User user = userAccessor.getUser(username);
        return user != null && userAccessor.authenticate(user.getName(), password);
    }

    /**
     * Returns whether the user is in the specify group
     *
     * @param username The username to check
     * @param group The group to check
     * @return True if the user is in the specified group
     */
    public boolean isUserInGroup(final String username, final String group)
    {
        return userAccessor.hasMembership(group, username);
    }

    public String getRemoteUsername(final HttpServletRequest request)
    {
        // TODO Implement SAL-16
        return getRemoteUsername();
    }

    public Principal resolve(final String username) throws UserResolutionException
    {
        final User user = userAccessor.getUser(username);
        if (user == null)
        {
            throw new UserResolutionException("User '" + username + "' doesn't exist.");
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