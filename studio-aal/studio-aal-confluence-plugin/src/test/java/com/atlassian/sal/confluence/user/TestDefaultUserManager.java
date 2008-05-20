package com.atlassian.sal.confluence.user;

import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.sal.confluence.user.DefaultUserManager;
import com.atlassian.user.User;
import junit.framework.TestCase;
import org.easymock.MockControl;

/**
 */
public class TestDefaultUserManager extends TestCase
{
    public void testGetRemoteUsername()
    {
        DefaultUserManager defaultUserManager = new DefaultUserManager();
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
        DefaultUserManager defaultUserManager = new DefaultUserManager();

        final MockControl mockUserControl = MockControl.createControl(User.class);
        final User mockUser = (User) mockUserControl.getMock();
        mockUserControl.replay();

        final MockControl mockUser2Control = MockControl.createControl(User.class);
        final User mockUser2 = (User) mockUser2Control.getMock();
        mockUser2Control.replay();

        final MockControl mockUserAccessorControl = MockControl.createControl(UserAccessor.class);
        final UserAccessor mockUserAccessor = (UserAccessor) mockUserAccessorControl.getMock();
        mockUserAccessor.getUser("tommy");
        mockUserAccessorControl.setReturnValue(null);
        mockUserAccessor.getUser("noaccess");
        mockUserAccessorControl.setReturnValue(mockUser);
        mockUserAccessor.getUser("sysadmin");
        mockUserAccessorControl.setReturnValue(mockUser2);
        mockUserAccessorControl.replay();

        final MockControl mockPermissionManagerControl = MockControl.createControl(PermissionManager.class);
        final PermissionManager mockPermissionManager = (PermissionManager) mockPermissionManagerControl.getMock();

        mockPermissionManager.isConfluenceAdministrator(mockUser);
        mockPermissionManagerControl.setReturnValue(false);

        mockPermissionManager.isConfluenceAdministrator(mockUser2);
        mockPermissionManagerControl.setReturnValue(true);
        mockPermissionManagerControl.replay();

        defaultUserManager.setUserAccessor(mockUserAccessor);
        defaultUserManager.setPermissionManager(mockPermissionManager);

        boolean isSystemAdmin = defaultUserManager.isSystemAdmin("tommy");
        assertFalse(isSystemAdmin);

        isSystemAdmin = defaultUserManager.isSystemAdmin("noaccess");
        assertFalse(isSystemAdmin);

        isSystemAdmin = defaultUserManager.isSystemAdmin("sysadmin");
        assertTrue(isSystemAdmin);

        mockPermissionManagerControl.verify();
        mockUserAccessorControl.verify();
        mockUserControl.verify();
        mockUser2Control.verify();
    }

    public void testAuthenticate()
    {
        DefaultUserManager defaultUserManager = new DefaultUserManager();

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

        defaultUserManager.setUserAccessor(mockUserAccessor);

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
