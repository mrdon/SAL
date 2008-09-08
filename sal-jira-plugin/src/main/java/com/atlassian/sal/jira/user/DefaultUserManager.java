package com.atlassian.sal.jira.user;

import org.apache.log4j.Logger;

import com.atlassian.jira.security.GlobalPermissionManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.Permissions;
import com.atlassian.sal.api.user.UserManager;
import com.opensymphony.user.EntityNotFoundException;
import com.opensymphony.user.User;

/**
 * OSUser based user operations
 */
public class DefaultUserManager implements UserManager
{
    private static final Logger log = Logger.getLogger(DefaultUserManager.class);
    private final GlobalPermissionManager globalPermissionManager;
    private final JiraAuthenticationContext authenticationContext;

    public DefaultUserManager(GlobalPermissionManager globalPermissionManager,
        JiraAuthenticationContext authenticationContext)
    {
        this.globalPermissionManager = globalPermissionManager;
        this.authenticationContext = authenticationContext;
    }

    public String getRemoteUsername()
    {
        User user = authenticationContext.getUser();
        if (user != null)
        {
            return user.getName();
        }
        return null;
    }

    public boolean isSystemAdmin(String username)
    {
        try
        {
            User user = getUser(username);
            return globalPermissionManager.hasPermission(Permissions.SYSTEM_ADMIN, user);
        }
        catch (EntityNotFoundException e)
        {
            //no user found.  Therefore no admin permission.
            return false;
        }
    }

    public boolean authenticate(String username, String password)
    {
        try
        {
            User user = getUser(username);
            return user.authenticate(password);
        }
        catch (EntityNotFoundException e)
        {
            log.debug("Could not find user to authenticate: " + e);
        }

        return false;
    }

    /**
     * Returns whether the user is in the specify group
     *
     * @param username The username to check
     * @param group The group to check
     * @return True if the user is in the specified group
     */
    public boolean isUserInGroup(String username, String group)
    {
        try
        {
            return getUser(username).inGroup(group);
        }
        catch (EntityNotFoundException enfe)
        {
            return false;
        }
    }

    //package level protected for testing
    User getUser(String username) throws EntityNotFoundException
    {
        return com.opensymphony.user.UserManager.getInstance().getUser(username);
    }
}
