package com.atlassian.sal.jira.lifecycle;

import com.atlassian.jira.util.JiraUtils;
import com.atlassian.sal.core.lifecycle.DefaultLifecycleManager;
import com.atlassian.plugin.event.PluginEventManager;

/**
 *
 */
public class JiraLifecycleManager extends DefaultLifecycleManager
{
    public JiraLifecycleManager(PluginEventManager pluginEventManager)
    {
        super(pluginEventManager);
    }

    public boolean isApplicationSetUp()
    {
        return JiraUtils.isSetup();
    }
}
