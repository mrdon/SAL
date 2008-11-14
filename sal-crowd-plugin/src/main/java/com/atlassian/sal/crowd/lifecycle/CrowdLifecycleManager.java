package com.atlassian.sal.crowd.lifecycle;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.sal.api.component.ComponentLocator;
import com.atlassian.sal.api.lifecycle.LifecycleManager;
import com.atlassian.sal.core.lifecycle.DefaultLifecycleManager;

public class CrowdLifecycleManager extends DefaultLifecycleManager implements ApplicationContextAware
{

	public boolean isApplicationSetUp()
	{
		return BootstrapUtils.getBootstrapManager().isSetupComplete();
	}

	@SuppressWarnings("unchecked")
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
	{
    	final Map<String, PluginEventManager> beansOfType = applicationContext.getBeansOfType(PluginEventManager.class);
    	final PluginEventManager pluginEventManager = beansOfType.values().iterator().next();
    	pluginEventManager.register(new Plugin2Listener());
	}

	public static class Plugin2Listener
	{
		@PluginEventListener
		public void start(Object o)
		{
			final LifecycleManager lifecycleManager = ComponentLocator.getComponent(LifecycleManager.class);
			lifecycleManager.start();
		}
	}

}
