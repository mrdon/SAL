package com.atlassian.sal.confluence.user;

import com.atlassian.confluence.core.InsufficientPrivilegeException;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.core.exception.InfrastructureException;
import com.atlassian.sal.api.SalException;
import com.atlassian.sal.api.security.InsufficientPrivilegesException;
import com.atlassian.sal.api.user.UserAlreadyExistsException;
import com.atlassian.sal.api.user.UserDoesNotExistException;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.user.User;
import com.atlassian.user.impl.DuplicateEntityException;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;

/** Authenticates a user against UserAccessor in Confluence. */
public class DefaultUserManager implements UserManager
{
    private UserAccessor userAccessor;
    private PermissionManager permissionManager;

    public String getRemoteUsername()
    {
        final User user = AuthenticatedUserThreadLocal.getUser();
        if (user != null)
        {
            return user.getName();
        }
        return null;
    }

    public boolean isSystemAdmin(final String username)
    {
        final User user = userAccessor.getUser(username);
        return user != null && permissionManager.isConfluenceAdministrator(user);
    }

    public boolean authenticate(final String username, final String password)
    {
        final User user = userAccessor.getUser(username);
        return user != null && userAccessor.authenticate(user.getName(), password);
    }

    public com.atlassian.sal.api.user.User getUser(String username)
    {
        final User user = userAccessor.getUser(username);

        if (user == null)
        {
            throw new UserDoesNotExistException(username);
        }

        return new AtlassianUserAdaptor(user);
    }

    /**
     * @throws SalException if an underlying InfrastructureException was thrown by Confluence
     * @see com.atlassian.sal.api.user.UserManager#createUser(com.atlassian.sal.api.user.User)
     */
    public com.atlassian.sal.api.user.User createUser(com.atlassian.sal.api.user.User user)
    {
        final User createdUser;
        try
        {
            createdUser = userAccessor.addUser(user.getUsername(), user.getPassword(), user.getEmailAddress(),
                    buildFullName(user), new String[]{UserAccessor.GROUP_CONFLUENCE_USERS});
        }
        catch (InfrastructureException e)
        {
            if (e.getCause() instanceof DuplicateEntityException)
            {
                throw new UserAlreadyExistsException(user.getUsername(), e);
            }
            else
            {
                throw new SalException(e);
            }
        }
        catch (InsufficientPrivilegeException e)
        {
            throw new InsufficientPrivilegesException(e);
        }
        return new AtlassianUserAdaptor(createdUser);
    }

    private String buildFullName(com.atlassian.sal.api.user.User user)
    {
        return user.getFirstName() + " " + user.getLastName();
    }

    public com.atlassian.sal.api.user.User updateUser(com.atlassian.sal.api.user.User user)
    {
        final User currentUser = getUserOrThrowException(user.getUsername());

        currentUser.setEmail(user.getEmailAddress());
        currentUser.setFullName(buildFullName(user));

        try
        {
            userAccessor.saveUser(currentUser);
        }
        catch (InfrastructureException e)
        {
            throw new SalException(e);
        }

        return new AtlassianUserAdaptor(currentUser);
    }

    public void removeUser(String username)
    {
        try
        {
            userAccessor.removeUser(getUserOrThrowException(username));
        }
        catch (InsufficientPrivilegeException e)
        {
            throw new InsufficientPrivilegesException(e);
        }
    }

    private User getUserOrThrowException(String username)
    {
        final User currentUser = userAccessor.getUser(username);
        if (currentUser == null)
        {
            throw new UserDoesNotExistException(username);
        }
        return currentUser;
    }

    /**
     * Returns whether the user is in the specify group
     * @param username The username to check
     * @param group The group to check
     * @return True if the user is in the specified group
     */
    public boolean isUserInGroup(final String username, final String group)
    {
        return userAccessor.hasMembership(group, username);
    }

    public void setUserAccessor(final UserAccessor userAccessor)
    {
        this.userAccessor = userAccessor;
    }

    public void setPermissionManager(final PermissionManager permissionManager)
    {
        this.permissionManager = permissionManager;
    }

    public String getRemoteUsername(final HttpServletRequest request)
    {
        // TODO Implement SAL-16
        return getRemoteUsername();
    }

    private static class AtlassianUserAdaptor implements com.atlassian.sal.api.user.User
    {
        private final User atlassianUser;

        AtlassianUserAdaptor(final User atlassianUser)
        {
            this.atlassianUser = atlassianUser;
        }

        public String getUsername()
        {
            return atlassianUser.getName();
        }

        public String getEmailAddress()
        {
            return atlassianUser.getEmail();
        }

        public String getFirstName()
        {
            return StringUtils.substringBefore(atlassianUser.getFullName(), " ");
        }

        public String getLastName()
        {
            return StringUtils.substringAfter(atlassianUser.getFullName(), " ");
        }

        public String getPassword()
        {
            return null;
        }
    }
}