package com.atlassian.sal.confluence.lifecycle;

import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.sal.core.lifecycle.DefaultLifecycleManager;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.plugin.event.PluginEventManager;

import java.util.List;
import java.util.Collections;

public class ConfluenceLifecycleManager extends DefaultLifecycleManager
{
    public ConfluenceLifecycleManager(PluginEventManager eventManager)
    {
        // todo: fix this to pass in the correct lifecycle aware list
        super(eventManager, Collections.<LifecycleAware>emptyList());
    }

    public boolean isApplicationSetUp()
	{
		return BootstrapUtils.getBootstrapManager().isSetupComplete();
	}
}
