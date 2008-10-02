package com.atlassian.sal.refimpl.upgrade;

import java.util.Collections;

import com.atlassian.plugin.PluginAccessor;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.upgrade.PluginUpgradeTask;
import com.atlassian.sal.core.upgrade.DefaultPluginUpgradeManager;

public class RefImplPluginUpgradeManager extends DefaultPluginUpgradeManager
{
    public RefImplPluginUpgradeManager(TransactionTemplate transactionTemplate,
            PluginAccessor pluginAccessor,
            PluginSettingsFactory pluginSettingsFactory)
    {
        super(Collections.<PluginUpgradeTask>emptyList(), transactionTemplate, pluginAccessor, pluginSettingsFactory);
    }
}
