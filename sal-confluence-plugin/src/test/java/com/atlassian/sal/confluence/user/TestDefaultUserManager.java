package com.atlassian.sal.confluence.user;

import junit.framework.TestCase;

import static org.easymock.EasyMock.*;

import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.user.User;
import org.easymock.MockControl;

/**
 */
public class TestDefaultUserManager extends TestCase
{
    public void testGetRemoteUsername()
    {
        final DefaultUserManager defaultUserManager = new DefaultUserManager(null, null);
        String username = defaultUserManager.getRemoteUsername();
        assertNull(username);

        final MockControl mockUserControl = MockControl.createControl(User.class);
        final User mockUser = (User) mockUserControl.getMock();
        mockUser.getName();
        mockUserControl.setReturnValue("johnwest");
        mockUserControl.replay();
        AuthenticatedUserThreadLocal.setUser(mockUser);

        username = defaultUserManager.getRemoteUsername();
        assertEquals("johnwest", username);
        mockUserControl.verify();
    }

    public void testIsSystemAdminNoUser()
    {
        final User mockUser = createMock(User.class);
        final User mockUser2 = createMock(User.class);
        final UserAccessor mockUserAccessor = createMock(UserAccessor.class);
        final PermissionManager permissionManager = createMock(PermissionManager.class);

        expect(mockUserAccessor.getUser("tommy")).andReturn(null);
        expect(mockUserAccessor.isSuperUser(mockUser)).andReturn(false);
        expect(mockUserAccessor.getUser("noaccess")).andReturn(mockUser);
        expect(mockUserAccessor.isSuperUser(mockUser)).andReturn(false);
        expect(mockUserAccessor.getUser("sysadmin")).andReturn(mockUser2);
        expect(mockUserAccessor.isSuperUser(mockUser2)).andReturn(true);
        expect(permissionManager.hasPermission(mockUser, Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM)).andReturn(false);
        expect(permissionManager.hasPermission(mockUser2, Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM)).andReturn(true);

        replay(mockUser, mockUser2, mockUserAccessor, permissionManager);

        final DefaultUserManager defaultUserManager = new DefaultUserManager(mockUserAccessor, permissionManager);
        assertFalse(defaultUserManager.isSystemAdmin("tommy"));
        assertFalse(defaultUserManager.isSystemAdmin("noaccess"));
        assertTrue(defaultUserManager.isSystemAdmin("sysadmin"));

        verify(mockUser, mockUser2);
    }

    public void testIsAdmin()
    {
        final MockControl mockUserControl = MockControl.createControl(User.class);
        final User mockAdminUser = (User) mockUserControl.getMock();
        mockUserControl.replay();

        final MockControl mockUserAccessorControl = MockControl.createControl(UserAccessor.class);
        final UserAccessor mockUserAccessor = (UserAccessor) mockUserAccessorControl.getMock();


        mockUserAccessor.getUser("tommy");
        mockUserAccessorControl.setReturnValue(mockAdminUser);
        mockUserAccessor.isSuperUser(mockAdminUser);
        mockUserAccessorControl.setReturnValue(false);

        mockUserAccessorControl.replay();

        final MockControl mockPermissionManagerControl = MockControl.createControl(PermissionManager.class);
        final PermissionManager permissionManager = (PermissionManager) mockPermissionManagerControl.getMock();


        permissionManager.isConfluenceAdministrator(mockAdminUser);
        mockPermissionManagerControl.setReturnValue(true);
        mockPermissionManagerControl.replay();

        final DefaultUserManager defaultUserManager = new DefaultUserManager(mockUserAccessor, permissionManager);

        boolean isAdmin = defaultUserManager.isAdmin("tommy");
        assertTrue(isAdmin);

        mockUserControl.verify();
    }

    public void testIsAdminNoUser()
    {

        final MockControl mockUserControl = MockControl.createControl(User.class);
        final User mockNoAccessUser = (User) mockUserControl.getMock();
        mockUserControl.replay();

        final MockControl mockUserAccessorControl = MockControl.createControl(UserAccessor.class);
        final UserAccessor mockUserAccessor = (UserAccessor) mockUserAccessorControl.getMock();

        mockUserAccessor.getUser("noaccess");
        mockUserAccessorControl.setReturnValue(mockNoAccessUser);
        mockUserAccessor.isSuperUser(mockNoAccessUser);
        mockUserAccessorControl.setReturnValue(false);

        mockUserAccessorControl.replay();

        final MockControl mockPermissionManagerControl = MockControl.createControl(PermissionManager.class);
        final PermissionManager permissionManager = (PermissionManager) mockPermissionManagerControl.getMock();


        permissionManager.isConfluenceAdministrator(mockNoAccessUser);
        mockPermissionManagerControl.setReturnValue(false);
        mockPermissionManagerControl.replay();

        final DefaultUserManager defaultUserManager = new DefaultUserManager(mockUserAccessor, permissionManager);

        boolean isAdmin = defaultUserManager.isAdmin("noaccess");
        assertFalse(isAdmin);

        mockUserControl.verify();
    }

    public void testAuthenticate()
    {

        final MockControl mockUserControl = MockControl.createControl(User.class);
        final User mockUser = (User) mockUserControl.getMock();
        mockUser.getName();
        mockUserControl.setReturnValue("noaccess");
        mockUserControl.replay();

        final MockControl mockUser2Control = MockControl.createControl(User.class);
        final User mockUser2 = (User) mockUser2Control.getMock();
        mockUser2.getName();
        mockUser2Control.setReturnValue("sysadmin");
        mockUser2Control.replay();

        final MockControl mockUserAccessorControl = MockControl.createControl(UserAccessor.class);
        final UserAccessor mockUserAccessor = (UserAccessor) mockUserAccessorControl.getMock();
        mockUserAccessor.getUser("tommy");
        mockUserAccessorControl.setReturnValue(null);
        mockUserAccessor.getUser("noaccess");
        mockUserAccessorControl.setReturnValue(mockUser);
        mockUserAccessor.authenticate("noaccess", "imabadbadpassword");
        mockUserAccessorControl.setReturnValue(false);
        mockUserAccessor.getUser("sysadmin");
        mockUserAccessorControl.setReturnValue(mockUser2);
        mockUserAccessor.authenticate("sysadmin", "password");
        mockUserAccessorControl.setReturnValue(true);
        mockUserAccessorControl.replay();

        final DefaultUserManager defaultUserManager = new DefaultUserManager(mockUserAccessor, null);

        boolean isAuthenticated = defaultUserManager.authenticate("tommy", "dontmatteruserdoesntexist");
        assertFalse(isAuthenticated);

        isAuthenticated = defaultUserManager.authenticate("noaccess", "imabadbadpassword");
        assertFalse(isAuthenticated);

        isAuthenticated = defaultUserManager.authenticate("sysadmin", "password");
        assertTrue(isAuthenticated);

        mockUserAccessorControl.verify();
        mockUser2Control.verify();
    }

}
