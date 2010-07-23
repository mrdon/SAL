package com.atlassian.sal.ctk.test;

import java.util.Collection;

import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.upgrade.PluginUpgradeManager;
import com.atlassian.sal.api.upgrade.PluginUpgradeTask;
import com.atlassian.sal.ctk.CtkTest;
import com.atlassian.sal.ctk.CtkTestResults;

public class PluginUpgradeManagerTest implements CtkTest, PluginUpgradeTask
{
    private final PluginUpgradeManager upgradeManager;
    private final PluginSettingsFactory pluginSettingsFactory;
    private static boolean called = false;

    public PluginUpgradeManagerTest(final PluginUpgradeManager upgradeManager, final PluginSettingsFactory pluginSettingsFactory)
    {
        this.upgradeManager = upgradeManager;
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    public void execute(final CtkTestResults results)
    {
        results.assertTrue("PluginUpgradeManager should be injectable", upgradeManager != null);
        results.assertTrueOrWarn("Upgrade task should have been called unless not first page load", called);
        called = false;
        upgradeManager.upgrade();
        results.assertTrueOrWarn("Upgrade task should not have been called after already upgraded unless not first page load)", !called);

        // Yes, we just happen to know how to clear the data build number....
        pluginSettingsFactory.createGlobalSettings().remove(getPluginKey()+":build");
    }

    public int getBuildNumber()
    {
        return 1;
    }

    public String getShortDescription()
    {
        return "foo";
    }

    public Collection<Message> doUpgrade() throws Exception
    {
        called = true;
        return null;
    }

    public String getPluginKey()
    {
        return "com.atlassian.sal.ctk";
    }
}