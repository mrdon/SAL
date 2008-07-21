package com.atlassian.sal.refimpl.user;

import org.apache.log4j.Logger;

import com.atlassian.sal.api.user.UserManager;

/**
 * Pretends the 'someUser' is logged in and is an admin
 */
public class DefaultUserManager implements UserManager
{
    private static final Logger log = Logger.getLogger(DefaultUserManager.class);
    boolean loggedin = false;

    public String getRemoteUsername()
    {
        if (loggedin)
            return "admin";
        else
            return null;
    }

    public boolean isSystemAdmin(String username)
    {
        return "admin".equals(username);
    }

    public boolean authenticate(String username, String password)
    {
        if ("admin".equals(username) && "admin".equals(password))
        {
            loggedin = true;
        }
        return loggedin;
    }

}
