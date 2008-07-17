package com.atlassian.sal.jira.lifecycle;

import com.atlassian.jira.util.JiraUtils;
import com.atlassian.jira.extension.Startable;
import com.atlassian.sal.core.lifecycle.DefaultLifecycleManager;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.plugin.event.PluginEventManager;

import java.util.List;

public class JiraLifecycleManager extends DefaultLifecycleManager implements Startable
{
    public JiraLifecycleManager(PluginEventManager eventManager, List<LifecycleAware> listeners)
    {
        // don't register self with event manager
        super(null, listeners);
    }

    public boolean isApplicationSetUp()
	{
		return JiraUtils.isSetup();
	}
	
}
