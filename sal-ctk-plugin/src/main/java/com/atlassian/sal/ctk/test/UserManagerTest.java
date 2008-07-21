package com.atlassian.sal.ctk.test;

import com.atlassian.sal.ctk.CtkTest;
import com.atlassian.sal.ctk.CtkTestResults;
import com.atlassian.sal.api.component.ComponentLocator;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.plugin.PluginManager;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class UserManagerTest implements CtkTest
{
    private final UserManager userManager;

    public UserManagerTest(UserManager userManager) {this.userManager = userManager;}

    public void execute(CtkTestResults results)
    {
        results.assertTrue("UserManager should be injectable", userManager != null);

        results.assertTrueOrWarn("Should return null for username when not logged in", userManager.getRemoteUsername() == null);
        results.assertTrueOrWarn("Should be able to login with admin/admin", userManager.authenticate("admin", "admin"));

        results.assertTrueOrWarn("Should have username of admin", "admin".equals(userManager.getRemoteUsername()));

        results.assertTrueOrWarn("admin user should be sysadmin", userManager.isSystemAdmin("admin"));

        results.assertTrue("somedumbadmin user should not be sysadmin", !userManager.isSystemAdmin("somedumbadmin"));
    }
}