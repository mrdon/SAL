package com.atlassian.sal.api.upgrade;

import java.util.List;

import com.atlassian.sal.api.message.Message;

/**
 * Upgrades plugins using their defined build number
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
