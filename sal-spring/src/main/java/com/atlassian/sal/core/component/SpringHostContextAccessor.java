package com.atlassian.sal.core.component;

import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.BeansException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.HashMap;

import com.atlassian.sal.spi.HostContextAccessor;

/**
 * Implements the host context accessor for Spring environments
 */
public class SpringHostContextAccessor implements HostContextAccessor, ApplicationContextAware
{
    private ApplicationContext applicationContext;
    private final PlatformTransactionManager transactionManager;
    private static final Log log = LogFactory.getLog(SpringHostContextAccessor.class);

    public SpringHostContextAccessor(PlatformTransactionManager transactionManager)
    {
        this.transactionManager = transactionManager;
    }

    public <T> Map<String, T> getComponentsOfType(Class<T> iface)
    {
        try
		{
        	return applicationContext.getBeansOfType(iface);
		} catch (RuntimeException ex)
		{
			// This exception will start occuring after removing bean from the context (eg: disabling plugin)
			// until next restart of Confluence. See https://studio.atlassian.com/browse/JST-509
			log.debug(ex,ex);
            Map<String, T> results = new HashMap<String, T>();
            if (applicationContext instanceof AbstractApplicationContext)
            {
                AbstractApplicationContext abstractApplicationContext = (AbstractApplicationContext) applicationContext;
                ConfigurableListableBeanFactory beanFactory = abstractApplicationContext.getBeanFactory();
                if (beanFactory instanceof DefaultListableBeanFactory)
                {
                    DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) beanFactory;
                    String[] beanDefinitionNames = defaultListableBeanFactory.getBeanDefinitionNames();
                    for (int i = 0; i < beanDefinitionNames.length; i++)
                    {
                        String beanName = beanDefinitionNames[i];
                        try
                        {
                            Object bean = defaultListableBeanFactory.getBean(beanName);
                            if (bean != null && iface.isAssignableFrom(bean.getClass()))
                            {
                                results.put(beanName, (T) bean);
                            }
                        } catch (BeansException e)
                        {
                            // ignore
                        }
                    }
                }
            }
            return results;
        }



    }

    public Object doInTransaction(final HostTransactionCallback callback)
    {
        final org.springframework.transaction.support.TransactionTemplate txTemplate =
                new org.springframework.transaction.support.TransactionTemplate(transactionManager, getTransactionDefinition());
        return txTemplate.execute(new org.springframework.transaction.support.TransactionCallback()
        {
            public Object doInTransaction(TransactionStatus transactionStatus)
            {
                try
                {
                    return callback.doInTransaction();
                }
                catch (RuntimeException e)
                {
                    //rollback if something weird happened.
                    transactionStatus.setRollbackOnly();
                    throw e;
                }
            }
        });
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }

    protected DefaultTransactionDefinition getTransactionDefinition()
    {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("PluginReadWriteTx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setReadOnly(false);
        return def;
    }



}

