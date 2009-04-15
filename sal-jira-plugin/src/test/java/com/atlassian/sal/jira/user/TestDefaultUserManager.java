package com.atlassian.sal.jira.user;

import com.atlassian.jira.security.GlobalPermissionManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.sal.jira.MockProviderAccessor;
import com.opensymphony.user.EntityNotFoundException;
import com.opensymphony.user.User;
import junit.framework.Assert;
import junit.framework.TestCase;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.easymock.classextension.EasyMock.createMock;

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

        final JiraAuthenticationContext mockJiraAuthenticationContext = createMock(JiraAuthenticationContext.class);
        expect(mockJiraAuthenticationContext.getUser()).andReturn(null);
        expect(mockJiraAuthenticationContext.getUser()).andReturn(mockUser);

        replay(mockJiraAuthenticationContext);
        DefaultUserManager defaultUserManager = new DefaultUserManager(null, mockJiraAuthenticationContext, null);
        String username = defaultUserManager.getRemoteUsername();
        assertNull(username);

        username = defaultUserManager.getRemoteUsername();
        assertEquals("tommy", username);

        verify(mockJiraAuthenticationContext);
    }

    public void testIsSystemAdminNoUser()
    {
        final UserUtil mockUserUtil = createMock(UserUtil.class);
        expect(mockUserUtil.getUser("tommy")).andReturn(null);
        replay(mockUserUtil);

        DefaultUserManager defaultUserManager = new DefaultUserManager(null, null, mockUserUtil);

        boolean systemAdmin = defaultUserManager.isSystemAdmin("tommy");
        assertFalse(systemAdmin);

        verify(mockUserUtil);
    }

    public void testIsSystemAdminNoPermissions()
    {
        MockProviderAccessor mpa = new MockProviderAccessor();
        final User mockUser = new User("tommy", mpa);

        final GlobalPermissionManager mockGlobalPermissionManager = createMock(GlobalPermissionManager.class);
        expect(mockGlobalPermissionManager.hasPermission(44, mockUser)).andReturn(false);

        final UserUtil mockUserUtil = createMock(UserUtil.class);
        expect(mockUserUtil.getUser("tommy")).andReturn(mockUser);
        replay(mockUserUtil, mockGlobalPermissionManager);

        DefaultUserManager defaultUserManager = new DefaultUserManager(mockGlobalPermissionManager, null, mockUserUtil);

        boolean systemAdmin = defaultUserManager.isSystemAdmin("tommy");
        assertFalse(systemAdmin);

        verify(mockUserUtil, mockGlobalPermissionManager);
    }

    public void testIsSystemAdmin()
    {
        MockProviderAccessor mpa = new MockProviderAccessor();
        final User mockUser = new User("tommy", mpa);

        final GlobalPermissionManager mockGlobalPermissionManager = createMock(GlobalPermissionManager.class);
        expect(mockGlobalPermissionManager.hasPermission(44, mockUser)).andReturn(true);

        final UserUtil mockUserUtil = createMock(UserUtil.class);
        expect(mockUserUtil.getUser("tommy")).andReturn(mockUser);
        replay(mockUserUtil, mockGlobalPermissionManager);

        DefaultUserManager defaultUserManager = new DefaultUserManager(mockGlobalPermissionManager, null, mockUserUtil)
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

        verify(mockUserUtil, mockGlobalPermissionManager);
    }

    public void testGetRemoteUserRequest()
    {
        MockProviderAccessor mpa = new MockProviderAccessor();
        final User mockUser = new User("tommy", mpa);

        final HttpSession mockHttpSession = createMock(HttpSession.class);
        expect(mockHttpSession.getAttribute("seraph_defaultauthenticator_user")).andReturn(mockUser);

        final HttpServletRequest mockHttpServletRequest = createMock(HttpServletRequest.class);
        expect(mockHttpServletRequest.getSession(false)).andReturn(mockHttpSession);
        replay(mockHttpSession, mockHttpServletRequest);

        DefaultUserManager defaultUserManager = new DefaultUserManager(null, null, null);
        final String remoteUsername = defaultUserManager.getRemoteUsername(mockHttpServletRequest);
        assertEquals("tommy", remoteUsername);

        verify(mockHttpServletRequest, mockHttpSession);
    }

    public void testGetRemoteUserRequestNoUser()
    {
        final HttpSession mockHttpSession = createMock(HttpSession.class);
        expect(mockHttpSession.getAttribute("seraph_defaultauthenticator_user")).andReturn(null);

        final HttpServletRequest mockHttpServletRequest = createMock(HttpServletRequest.class);
        expect(mockHttpServletRequest.getSession(false)).andReturn(mockHttpSession);
        replay(mockHttpSession, mockHttpServletRequest);

        DefaultUserManager defaultUserManager = new DefaultUserManager(null, null, null);
        final String remoteUsername = defaultUserManager.getRemoteUsername(mockHttpServletRequest);
        assertNull(remoteUsername);

        verify(mockHttpServletRequest, mockHttpSession);
    }
}

