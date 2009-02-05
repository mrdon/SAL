package com.atlassian.sal.jira.user;

import com.atlassian.jira.security.GlobalPermissionManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.Permissions;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.seraph.auth.DefaultAuthenticator;
import com.opensymphony.user.EntityNotFoundException;
import com.opensymphony.user.User;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * OSUser based user operations
 */
public class DefaultUserManager implements UserManager
{
    private static final Logger log = Logger.getLogger(DefaultUserManager.class);
    private final GlobalPermissionManager globalPermissionManager;
    private final JiraAuthenticationContext authenticationContext;

    public DefaultUserManager(final GlobalPermissionManager globalPermissionManager,
        final JiraAuthenticationContext authenticationContext)
    {
        this.globalPermissionManager = globalPermissionManager;
        this.authenticationContext = authenticationContext;
    }

    public String getRemoteUsername()
    {
        final User user = authenticationContext.getUser();
        if (user != null)
        {
            return user.getName();
        }
        return null;
    }

    public boolean isSystemAdmin(final String username)
    {
        try
        {
            final User user = getUser(username);
            return globalPermissionManager.hasPermission(Permissions.SYSTEM_ADMIN, user);
        }
        catch (final EntityNotFoundException e)
        {
            //no user found.  Therefore no admin permission.
            return false;
        }
    }

    public boolean authenticate(final String username, final String password)
    {
        try
        {
            final User user = getUser(username);
            return user.authenticate(password);
        }
        catch (final EntityNotFoundException e)
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
    public boolean isUserInGroup(final String username, final String group)
    {
        try
        {
            return getUser(username).inGroup(group);
        }
        catch (final EntityNotFoundException enfe)
        {
            return false;
        }
    }

    //package level protected for testing
    User getUser(final String username) throws EntityNotFoundException
    {
        return com.opensymphony.user.UserManager.getInstance().getUser(username);
    }

    public String getRemoteUsername(final HttpServletRequest request)
    {
        final HttpSession session = request.getSession(false);
        if(session != null)
        {
            final User user = (User) session.getAttribute(DefaultAuthenticator.LOGGED_IN_KEY);
            if(user != null)
            {                
                return user.getName();
            }
        }

        return null;
    }
}
