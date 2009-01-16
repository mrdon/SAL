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
import com.atlassian.user.User;
import com.atlassian.user.impl.DuplicateEntityException;
import junit.framework.TestCase;
import org.apache.commons.lang.StringUtils;
import static org.mockito.Mockito.*;

public class TestDefaultUserManager extends TestCase
{
    private static final String TOMMY_USERNAME = "tommy";
    private static final String TOMMY_FULLNAME = "Tommy Tom";
    private static final String TOMMY_EMAIL = "tommy@example.com";

    private DefaultUserManager userManager;

    private User tommy;
    private User noAccess;
    private User sysAdmin;
    private UserAccessor userAccessor;

    @Override
    protected void setUp() throws Exception
    {
        userManager = new DefaultUserManager();

        tommy = mock(User.class);
        when(tommy.getName()).thenReturn(TOMMY_USERNAME);
        when(tommy.getFullName()).thenReturn(TOMMY_FULLNAME);
        when(tommy.getEmail()).thenReturn(TOMMY_EMAIL);

        noAccess = mock(User.class);
        when(noAccess.getName()).thenReturn("noAccess");

        sysAdmin = mock(User.class);
        when(sysAdmin.getName()).thenReturn("sysAdmin");

        userAccessor = mock(UserAccessor.class);
        when(userAccessor.getUser(tommy.getName())).thenReturn(null);
        when(userAccessor.getUser(noAccess.getName())).thenReturn(noAccess);
        when(userAccessor.getUser(sysAdmin.getName())).thenReturn(sysAdmin);

        userManager.setUserAccessor(userAccessor);
    }

    public void testGetRemoteUsernameWithNoUserInThreadLocalIsNull()
    {
        assertNull(userManager.getRemoteUsername());
    }

    public void testGetRemoteUsernameWithUserInThreadLocalIsNotNull()
    {
        AuthenticatedUserThreadLocal.setUser(tommy);

        assertEquals(tommy.getName(), userManager.getRemoteUsername());
    }

    public void testIsSystemAdminNoUser()
    {
        final PermissionManager permissionManager = mock(PermissionManager.class);
        when(permissionManager.isConfluenceAdministrator(noAccess)).thenReturn(false);
        when(permissionManager.isConfluenceAdministrator(sysAdmin)).thenReturn(true);

        userManager.setPermissionManager(permissionManager);

        assertFalse(userManager.isSystemAdmin(tommy.getName()));
        assertFalse(userManager.isSystemAdmin(noAccess.getName()));
        assertTrue(userManager.isSystemAdmin(sysAdmin.getName()));
    }

    public void testAuthenticate()
    {
        final String badPassword = "imabadbadpassword";
        final String goodPassword = "password";

        when(userAccessor.authenticate(noAccess.getName(), badPassword)).thenReturn(false);
        when(userAccessor.authenticate(sysAdmin.getName(), goodPassword)).thenReturn(true);

        assertFalse(userManager.authenticate(tommy.getName(), "dontmatteruserdoesntexist"));
        assertFalse(userManager.authenticate(noAccess.getName(), badPassword));
        assertTrue(userManager.authenticate(sysAdmin.getName(), goodPassword));
    }

    public void testCreateUserSucceeds()
    {
        final String password = "password";

        when(userAccessor.addUser(TOMMY_USERNAME, password, TOMMY_EMAIL, TOMMY_FULLNAME, new String[]{UserAccessor.GROUP_CONFLUENCE_USERS})).thenReturn(tommy);

        com.atlassian.sal.api.user.User userToCreate = getSalTommy(password);
        final com.atlassian.sal.api.user.User createdUser = userManager.createUser(userToCreate);

        assertEquals(userToCreate.getUsername(), createdUser.getUsername());
        assertEquals(userToCreate.getFirstName(), createdUser.getFirstName());
        assertEquals(userToCreate.getLastName(), createdUser.getLastName());
        assertEquals(userToCreate.getEmailAddress(), createdUser.getEmailAddress());
        assertNull(createdUser.getPassword());
    }

    public void testCreateUserWithNoPrivilege()
    {
        final String password = "password";
        final InsufficientPrivilegeException exception = new InsufficientPrivilegeException("");
        when(userAccessor.addUser(TOMMY_USERNAME, password, TOMMY_EMAIL, TOMMY_FULLNAME, new String[]{UserAccessor.GROUP_CONFLUENCE_USERS})).thenThrow(exception);
        try
        {
            userManager.createUser(getSalTommy(password));
            fail();
        }
        catch (InsufficientPrivilegesException e)
        {
            assertEquals(exception, e.getCause());
        }
    }

    public void testCreateDuplicateUser()
    {
        final String password = "password";
        when(userAccessor.addUser(TOMMY_USERNAME, password, TOMMY_EMAIL, TOMMY_FULLNAME, new String[]{UserAccessor.GROUP_CONFLUENCE_USERS})).thenThrow(new InfrastructureException(new DuplicateEntityException()));
        try
        {
            userManager.createUser(getSalTommy(password));
            fail();
        }
        catch (UserAlreadyExistsException e)
        {
        }
    }

    public void testGetUserWhoExists()
    {
        when(userAccessor.getUser(TOMMY_USERNAME)).thenReturn(tommy);

        final com.atlassian.sal.api.user.User user = userManager.getUser(TOMMY_USERNAME);

        assertEquals(TOMMY_USERNAME, user.getUsername());
        assertEquals(TOMMY_EMAIL, user.getEmailAddress());
    }

    public void testGetUserWhoDoesNotExist()
    {
        try
        {
            userManager.getUser(TOMMY_USERNAME);
            fail();
        }
        catch (UserDoesNotExistException e)
        {
            // Yay@!
        }
    }

    public void testUpdateUserWhoDoesNotExist()
    {
        try
        {
            userManager.updateUser(getSalTommy(null));
            fail();
        }
        catch (UserDoesNotExistException e)
        {
            // Yay@!
        }
    }

//    public void testUpdateUserWithNoPrivileges()
//    {
//        when(userAccessor.getUser())
//    }

    public void testUpdateUserWithAnUnderlyingInfrastructureException()
    {
        when(userAccessor.getUser(TOMMY_USERNAME)).thenReturn(tommy);
        doThrow(new InfrastructureException("")).when(userAccessor).saveUser(tommy);

        try
        {
            userManager.updateUser(getSalTommy(null));
            fail();
        }
        catch (SalException e)
        {
            // Yay@!
        }
    }

    public void testUpdateUserWhoDoesExist()
    {
        when(userAccessor.getUser(TOMMY_USERNAME)).thenReturn(tommy);

        userManager.updateUser(getSalTommy(null));

        verify(userAccessor).saveUser(tommy);
    }

    public void testRemoveUserWhoDoesNotExist()
    {
        try
        {
            userManager.removeUser(TOMMY_USERNAME);
            fail();
        }
        catch (UserDoesNotExistException e)
        {
            // Yay@!
        }
    }

    public void testRemoveUserWithNoPrivileges()
    {
        when(userAccessor.getUser(TOMMY_USERNAME)).thenReturn(tommy);

        doThrow(new InsufficientPrivilegeException(TOMMY_USERNAME)).when(userAccessor).removeUser(tommy);

        try
        {
            userManager.removeUser(TOMMY_USERNAME);
            fail();
        }
        catch (InsufficientPrivilegesException e)
        {
            // Yay@!
        }
    }

    public void testRemoveUserWhoDoesExist()
    {
        when(userAccessor.getUser(TOMMY_USERNAME)).thenReturn(tommy);

        userManager.removeUser(TOMMY_USERNAME);

        verify(userAccessor).removeUser(tommy);
    }

    private com.atlassian.sal.api.user.User getSalTommy(String password)
    {
        final String fisrtName = StringUtils.substringBefore(TOMMY_FULLNAME, " ");
        final String lastName = StringUtils.substringAfter(TOMMY_FULLNAME, " ");

        com.atlassian.sal.api.user.User userToCreate = mock(com.atlassian.sal.api.user.User.class);
        when(userToCreate.getUsername()).thenReturn(TOMMY_USERNAME);
        when(userToCreate.getFirstName()).thenReturn(fisrtName);
        when(userToCreate.getLastName()).thenReturn(lastName);
        when(userToCreate.getEmailAddress()).thenReturn(TOMMY_EMAIL);
        when(userToCreate.getPassword()).thenReturn(password);
        return userToCreate;
    }
}
