package com.atlassian.sal.jira.user;

import com.atlassian.jira.security.GlobalPermissionManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.Permissions;
import com.atlassian.sal.api.logging.Logger;
import com.atlassian.sal.api.logging.LoggerFactory;
import com.atlassian.sal.api.user.UserManager;
import com.opensymphony.user.EntityNotFoundException;
import com.opensymphony.user.User;

/** OSUser based user operations */
public class DefaultUserManager implements UserManager
{
    private static final Logger log = LoggerFactory.getLogger(DefaultUserManager.class);
    private final GlobalPermissionManager globalPermissionManager;
    private final JiraAuthenticationContext authenticationContext;

    public DefaultUserManager(GlobalPermissionManager globalPermissionManager, JiraAuthenticationContext authenticationContext)
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

    //package level protected for testing
    User getUser(String username) throws EntityNotFoundException
    {
        return com.opensymphony.user.UserManager.getInstance().getUser(username);
    }
}
