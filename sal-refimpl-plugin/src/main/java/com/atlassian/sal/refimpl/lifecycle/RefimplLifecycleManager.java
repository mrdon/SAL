package com.atlassian.sal.refimpl.lifecycle;

import com.atlassian.sal.core.lifecycle.DefaultLifecycleManager;


public class RefimplLifecycleManager extends DefaultLifecycleManager
{
	public boolean isApplicationSetUp()
	{
		return true;
	}
}
