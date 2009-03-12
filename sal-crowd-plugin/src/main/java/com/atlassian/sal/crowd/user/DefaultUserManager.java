package com.atlassian.sal.crowd.user;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import com.atlassian.crowd.model.user.UserAccessor;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserResolutionException;

/**
 * FishEye implementation of the UserManager
 */
public class DefaultUserManager implements UserManager
{

    private final UserAccessor userAccessor;

    public DefaultUserManager(final UserAccessor userAccessor)
    {
        this.userAccessor = userAccessor;
    }

    public String getRemoteUsername()
    {
        return userAccessor.getRemoteUsername();
    }

    public boolean isSystemAdmin(final String username)
    {
        return userAccessor.isSystemAdmin(username);
    }

    public boolean isUserInGroup(final String username, final String group)
    {
        throw new UnsupportedOperationException();
    }

    public boolean authenticate(final String username, final String password)
    {
        throw new UnsupportedOperationException();
    }

    public String getRemoteUsername(final HttpServletRequest request)
    {
        // TODO Implement SAL-16
        return getRemoteUsername();
    }

    public Principal resolve(final String username) throws UserResolutionException
    {
        throw new UnsupportedOperationException();
//        final User user = userAccessor.getUser(username);
//        if (user == null)
//        {
//            throw new UserResolutionException("User '" + username + "' doesn't exist.");
//        }
//        return new Principal()
//        {
//            public String getName()
//            {
//                return username;
//            }
//        };
    }}

