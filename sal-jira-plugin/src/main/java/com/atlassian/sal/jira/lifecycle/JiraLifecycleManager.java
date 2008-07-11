package com.atlassian.sal.jira.lifecycle;

import com.atlassian.jira.util.JiraUtils;
import com.atlassian.sal.core.lifecycle.DefaultLifecycleManager;

public class JiraLifecycleManager extends DefaultLifecycleManager
{

	public boolean isApplicationSetUp()
	{
		return JiraUtils.isSetup();
	}
	
}
