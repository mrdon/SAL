package com.atlassian.sal.refimpl.user;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.atlassian.seraph.auth.AuthenticationContext;
import com.atlassian.user.EntityException;
import com.atlassian.user.Group;
import com.atlassian.user.GroupManager;
import com.atlassian.user.User;
import com.atlassian.user.UserManager;
import com.atlassian.user.security.authentication.Authenticator;

/**
 * Pretends the 'someUser' is logged in and is an admin
 */
public class RefImplUserManager implements com.atlassian.sal.api.user.UserManager
{
    private final Logger log = Logger.getLogger(getClass());

    private final AuthenticationContext authenticationContext;
    private final GroupManager groupManager;
    private final UserManager userManager;
    private final Authenticator authenticator;

    public RefImplUserManager(AuthenticationContext authenticationContext, UserManager userManager,
        GroupManager groupManager, Authenticator authenticator)
    {
        this.authenticationContext = authenticationContext;
        this.userManager = userManager;
        this.groupManager = groupManager;
        this.authenticator = authenticator;
    }

    public String getRemoteUsername()
    {
        Principal user = authenticationContext.getUser();
        if (user == null)
            return null;
        return user.getName();
    }
    
    public String getRemoteUsername(HttpServletRequest request)
    {
        return request.getRemoteUser();
    }

    public boolean isSystemAdmin(String username)
    {
        return isUserInGroup(username, "administrators");
    }

    public boolean authenticate(String username, String password)
    {
        try
        {
            boolean authenticated = authenticator.authenticate(username, password);
            if (!authenticated)
            {
                log.info("Cannot login user '" + username + "' as they used an incorrect password");
            }
            return authenticated;
        }
        catch (EntityException e)
        {
            log.info("Cannot login user '" + username + "' as they do not exist.");
            return false;
        }
    }

    public boolean isUserInGroup(String username, String group)
    {
        try
        {
            User user = userManager.getUser(username);
            Group adminGroup = groupManager.getGroup(group);
            return groupManager.hasMembership(adminGroup, user);
        }
        catch (EntityException e)
        {
            return false;
        }
    }
}
