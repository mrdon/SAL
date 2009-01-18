package com.atlassian.sal.crowd.user;

import javax.servlet.http.HttpServletRequest;

import com.atlassian.crowd.model.user.UserAccessor;
import com.atlassian.sal.api.user.User;
import com.atlassian.sal.api.user.UserManager;

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


    public User getUser(final String username)
    {
        throw new UnsupportedOperationException();
    }

    public User createUser(final User user)
    {
        throw new UnsupportedOperationException();
    }

    public User updateUser(final User user)
    {
        throw new UnsupportedOperationException();
    }

    public void removeUser(final String username)
    {
        throw new UnsupportedOperationException();
    }

}

