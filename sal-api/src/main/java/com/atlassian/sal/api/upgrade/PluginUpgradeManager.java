package com.atlassian.sal.api.upgrade;

import com.atlassian.sal.api.message.Message;

import java.util.List;

/**
 * Upgrades plugins using their defined build number
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
