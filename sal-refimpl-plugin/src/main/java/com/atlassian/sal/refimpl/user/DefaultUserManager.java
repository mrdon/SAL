package com.atlassian.sal.refimpl.user;

import org.apache.log4j.Logger;

import com.atlassian.sal.api.user.UserManager;

/**
 * Pretends the 'someUser' is logged in and is an admin
 */
public class DefaultUserManager implements UserManager
{
    private static final Logger log = Logger.getLogger(DefaultUserManager.class);

    public String getRemoteUsername()
    {
        return "someUser";
    }

    public boolean isSystemAdmin(String username)
    {
        return true;
    }

    public boolean authenticate(String username, String password)
    {
        return true;
    }
    
    public boolean isUserInGroup(String username, String group)
    {
        return true;
    }

}
