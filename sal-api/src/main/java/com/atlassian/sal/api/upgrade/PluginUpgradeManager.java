package com.atlassian.sal.api.upgrade;

import com.atlassian.sal.api.message.Message;

import java.util.List;

/**
 * Upgrades plugins using their defined build number. This only guarantees that upgrade tasks will be run when
 * the plugins system is initialized. There is no guarantee that upgrade tasks will be run on plugin enablement or
 * plugin upgrade.
 *
 * @since 2.0
 */
public interface PluginUpgradeManager
{
    /**
     * Finds and upgrades all plugins, that implement PluginUpgradeTask
     *
     * @return A list of errors
     */
    List<Message> upgrade();
}
