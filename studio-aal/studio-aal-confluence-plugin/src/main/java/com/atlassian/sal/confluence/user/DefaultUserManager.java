package com.atlassian.sal.confluence.user;

import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.user.User;

/** Authenticates a user against UserAccessor in Confluence. */
public class DefaultUserManager implements UserManager
{
    private UserAccessor userAccessor;
    private PermissionManager permissionManager;

    public String getRemoteUsername()
    {
        User user = AuthenticatedUserThreadLocal.getUser();
        if (user != null)
        {
            return user.getName();
        }
        return null;
    }

    public boolean isSystemAdmin(String username)
    {
        final User user = userAccessor.getUser(username);
        return user != null && permissionManager.isConfluenceAdministrator(user);
    }

    public boolean authenticate(String username, String password)
    {
        final User user = userAccessor.getUser(username);
        return user != null && userAccessor.authenticate(user.getName(), password);
    }

    public void setUserAccessor(UserAccessor userAccessor)
    {
        this.userAccessor = userAccessor;
    }

    public void setPermissionManager(PermissionManager permissionManager)
    {
        this.permissionManager = permissionManager;
    }
}