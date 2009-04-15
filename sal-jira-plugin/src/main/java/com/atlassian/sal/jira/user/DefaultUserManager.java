package com.atlassian.sal.jira.user;

import com.atlassian.jira.security.GlobalPermissionManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.Permissions;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserResolutionException;
import com.atlassian.seraph.auth.DefaultAuthenticator;
import com.opensymphony.user.User;
import org.apache.log4j.Logger;

import java.security.Principal;
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
    private final UserUtil userUtil;

    public DefaultUserManager(final GlobalPermissionManager globalPermissionManager,
        final JiraAuthenticationContext authenticationContext, final UserUtil userUtil)
    {
        this.globalPermissionManager = globalPermissionManager;
        this.authenticationContext = authenticationContext;
        this.userUtil = userUtil;
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
        final User user = userUtil.getUser(username);
        return user != null && globalPermissionManager.hasPermission(Permissions.SYSTEM_ADMIN, user);
    }

    public boolean authenticate(final String username, final String password)
    {
        final User user = userUtil.getUser(username);
        if(user != null)
        {
            return user.authenticate(password);
        }
        else
        {
            log.debug("Could not find user to authenticate with username '" + username +"'");
            return false;
        }
    }

    public Principal resolve(final String username) throws UserResolutionException
    {
        return userUtil.getUser(username);
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
        final User user = userUtil.getUser(username);
        return user != null && user.inGroup(group);
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
