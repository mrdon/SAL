package com.atlassian.sal.refimpl.lifecycle;

import java.util.LinkedList;

import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.core.lifecycle.DefaultLifecycleManager;

public class RefimplLifecycleManager extends DefaultLifecycleManager
{
    public RefimplLifecycleManager()
    {
        setListeners(new LinkedList<LifecycleAware>());
    }
    
    public boolean isApplicationSetUp()
    {
        return true;
    }
}
