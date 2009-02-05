package com.atlassian.sal.jira.user;

import com.atlassian.jira.security.GlobalPermissionManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.sal.jira.MockProviderAccessor;
import com.opensymphony.user.EntityNotFoundException;
import com.opensymphony.user.User;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.easymock.MockControl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 */
public class TestDefaultUserManager extends TestCase
{
    public void testGetRemoteUsername()
    {
        MockProviderAccessor mpa = new MockProviderAccessor();
        User mockUser = new User("tommy", mpa);

        final MockControl mockJiraAuthenticationContextControl = MockControl.createControl(JiraAuthenticationContext.class);
        final JiraAuthenticationContext mockJiraAuthenticationContext = (JiraAuthenticationContext) mockJiraAuthenticationContextControl.getMock();
        mockJiraAuthenticationContext.getUser();
        mockJiraAuthenticationContextControl.setReturnValue(null);
        mockJiraAuthenticationContext.getUser();
        mockJiraAuthenticationContextControl.setReturnValue(mockUser);

        mockJiraAuthenticationContextControl.replay();
        DefaultUserManager defaultUserManager = new DefaultUserManager(null, mockJiraAuthenticationContext);
        String username = defaultUserManager.getRemoteUsername();
        assertNull(username);

        username = defaultUserManager.getRemoteUsername();
        assertEquals("tommy", username);

        mockJiraAuthenticationContextControl.verify();
    }

    public void testIsSystemAdminNoUser()
    {
        DefaultUserManager defaultUserManager = new DefaultUserManager(null, null)
        {
            //package level protected for testing
            User getUser(String username) throws EntityNotFoundException
            {
                Assert.assertEquals("tommy", username);
                throw new EntityNotFoundException("tommy not found!");
            }
        };

        boolean systemAdmin = defaultUserManager.isSystemAdmin("tommy");
        assertFalse(systemAdmin);
    }

    public void testIsSystemAdminNoPermissions()
    {
        MockProviderAccessor mpa = new MockProviderAccessor();
        final User mockUser = new User("tommy", mpa);

        final MockControl mockGlobalPermissionManagerControl = MockControl.createControl(GlobalPermissionManager.class);
        final GlobalPermissionManager mockGlobalPermissionManager = (GlobalPermissionManager) mockGlobalPermissionManagerControl.getMock();

        mockGlobalPermissionManager.hasPermission(44, mockUser);
        mockGlobalPermissionManagerControl.setReturnValue(false);
        mockGlobalPermissionManagerControl.replay();

        DefaultUserManager defaultUserManager = new DefaultUserManager(mockGlobalPermissionManager, null)
        {
            //package level protected for testing
            User getUser(String username) throws EntityNotFoundException
            {
                Assert.assertEquals("tommy", username);
                return mockUser;
            }
        };

        boolean systemAdmin = defaultUserManager.isSystemAdmin("tommy");
        assertFalse(systemAdmin);
        mockGlobalPermissionManagerControl.verify();
    }

    public void testIsSystemAdmin()
    {
        MockProviderAccessor mpa = new MockProviderAccessor();
        final User mockUser = new User("tommy", mpa);

        final MockControl mockGlobalPermissionManagerControl = MockControl.createControl(GlobalPermissionManager.class);
        final GlobalPermissionManager mockGlobalPermissionManager = (GlobalPermissionManager) mockGlobalPermissionManagerControl.getMock();

        mockGlobalPermissionManager.hasPermission(44, mockUser);
        mockGlobalPermissionManagerControl.setReturnValue(true);
        mockGlobalPermissionManagerControl.replay();

        DefaultUserManager defaultUserManager = new DefaultUserManager(mockGlobalPermissionManager, null)
        {
            //package level protected for testing
            User getUser(String username) throws EntityNotFoundException
            {
                Assert.assertEquals("tommy", username);
                return mockUser;
            }
        };

        boolean systemAdmin = defaultUserManager.isSystemAdmin("tommy");
        assertTrue(systemAdmin);
        mockGlobalPermissionManagerControl.verify();
    }

    public void testGetRemoteUserRequest()
    {
        MockProviderAccessor mpa = new MockProviderAccessor();
        final User mockUser = new User("tommy", mpa);

        MockControl mockHttpSessionControl = MockControl.createControl(HttpSession.class);
        HttpSession mockHttpSession = (HttpSession) mockHttpSessionControl.getMock();
        mockHttpSession.getAttribute("seraph_defaultauthenticator_user");
        mockHttpSessionControl.setReturnValue(mockUser);
        mockHttpSessionControl.replay();

        MockControl mockHttpServletRequestControl = MockControl.createControl(HttpServletRequest.class);
        HttpServletRequest mockHttpServletRequest = (HttpServletRequest) mockHttpServletRequestControl.getMock();
        mockHttpServletRequest.getSession(false);
        mockHttpServletRequestControl.setReturnValue(mockHttpSession);
        mockHttpServletRequestControl.replay();

        DefaultUserManager defaultUserManager = new DefaultUserManager(null, null);
        final String remoteUsername = defaultUserManager.getRemoteUsername(mockHttpServletRequest);
        assertEquals("tommy", remoteUsername);

        mockHttpSessionControl.verify();
        mockHttpServletRequestControl.verify();
    }

    public void testGetRemoteUserRequestNoUser()
    {

        MockControl mockHttpSessionControl = MockControl.createControl(HttpSession.class);
        HttpSession mockHttpSession = (HttpSession) mockHttpSessionControl.getMock();
        mockHttpSession.getAttribute("seraph_defaultauthenticator_user");
        mockHttpSessionControl.setReturnValue(null);
        mockHttpSessionControl.replay();

        MockControl mockHttpServletRequestControl = MockControl.createControl(HttpServletRequest.class);
        HttpServletRequest mockHttpServletRequest = (HttpServletRequest) mockHttpServletRequestControl.getMock();
        mockHttpServletRequest.getSession(false);
        mockHttpServletRequestControl.setReturnValue(mockHttpSession);
        mockHttpServletRequestControl.replay();

        DefaultUserManager defaultUserManager = new DefaultUserManager(null, null);
        final String remoteUsername = defaultUserManager.getRemoteUsername(mockHttpServletRequest);
        assertNull(remoteUsername);

        mockHttpSessionControl.verify();
        mockHttpServletRequestControl.verify();
    }
}

