package com.atlassian.sal.ctk.test;

import org.springframework.stereotype.Component;

import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.ctk.CtkTest;
import com.atlassian.sal.ctk.CtkTestResults;

@Component
public class UserManagerTest implements CtkTest
{
    private final UserManager userManager;

    public UserManagerTest(final UserManager userManager)
	{
		this.userManager = userManager;
	}

    public void execute(final CtkTestResults results)
    {
        results.assertTrue("UserManager should be injectable", userManager != null);

        final String remoteUsername = userManager.getRemoteUsername();
		results.assertTrueOrWarn("Should return null for username when not logged in. Currently logged user: " + remoteUsername, remoteUsername == null);
        results.assertTrueOrWarn("Should be able to login with admin/admin", userManager.authenticate("admin", "admin"));

        results.assertTrueOrWarn("Should have username of admin", "admin".equals(remoteUsername));

        results.assertTrueOrWarn("admin user should be sysadmin", userManager.isSystemAdmin("admin"));

        results.assertTrue("somedumbadmin user should not be sysadmin", !userManager.isSystemAdmin("somedumbadmin"));
    }
}