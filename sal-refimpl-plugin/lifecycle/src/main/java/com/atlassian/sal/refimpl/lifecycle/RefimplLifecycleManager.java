package com.atlassian.sal.refimpl.lifecycle;

import java.util.LinkedList;
import java.util.List;

import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.core.lifecycle.DefaultLifecycleManager;
import com.atlassian.plugin.event.PluginEventManager;

public class RefimplLifecycleManager extends DefaultLifecycleManager
{
    public RefimplLifecycleManager(PluginEventManager mgr)
    {
        super(mgr);
    }
    
    public boolean isApplicationSetUp()
    {
        return true;
    }
}
