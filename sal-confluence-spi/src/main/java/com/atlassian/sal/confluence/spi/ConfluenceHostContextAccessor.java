package com.atlassian.sal.confluence.spi;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.atlassian.sal.spi.HostContextAccessor;

public class ConfluenceHostContextAccessor implements HostContextAccessor, ApplicationContextAware
{
	private ApplicationContext applicationContext;

	public Object doInTransaction(HostTransactionCallback hostTransactionCallback)
	{
		return hostTransactionCallback.doInTransaction();
	}

	@SuppressWarnings("unchecked")
	public <T> Map<String, T> getComponentsOfType(Class<T> iface)
	{
		return applicationContext.getBeansOfType(iface);
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
	{
		this.applicationContext = applicationContext;
	}
}
