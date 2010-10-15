package com.atlassian.sal.core.component;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.atlassian.sal.spi.HostContextAccessor;

/**
 * Implements the host context accessor for Spring environments
 * @deprecated use {@link com.atlassian.sal.spring.component.SpringHostContextAccessor}
 * TODO remove this class when fisheye will ship with sal-2.0
 */
@Deprecated
public class SpringHostContextAccessor implements HostContextAccessor, ApplicationContextAware
{
    private ApplicationContext applicationContext;
    private final PlatformTransactionManager transactionManager;
    private static final Log log = LogFactory.getLog(SpringHostContextAccessor.class);

    public SpringHostContextAccessor(final PlatformTransactionManager transactionManager)
    {
        this.transactionManager = transactionManager;
    }

    @SuppressWarnings("unchecked")
	public <T> Map<String, T> getComponentsOfType(final Class<T> iface)
    {
        try
		{
        	return applicationContext.getBeansOfType(iface);
		} catch (final RuntimeException ex)
		{
			// This exception will start occuring after removing bean from the context (eg: disabling plugin)
			// until next restart of Confluence. See https://studio.atlassian.com/browse/JST-509
			log.debug(ex,ex);
            final Map<String, T> results = new HashMap<String, T>();
            if (applicationContext instanceof AbstractApplicationContext)
            {
                final AbstractApplicationContext abstractApplicationContext = (AbstractApplicationContext) applicationContext;
                final ConfigurableListableBeanFactory beanFactory = abstractApplicationContext.getBeanFactory();
                if (beanFactory instanceof DefaultListableBeanFactory)
                {
                    final DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) beanFactory;
                    final String[] beanDefinitionNames = defaultListableBeanFactory.getBeanDefinitionNames();
                    for (final String beanName : beanDefinitionNames)
					{
                        try
                        {
                            final Object bean = defaultListableBeanFactory.getBean(beanName);
                            if (bean != null && iface.isAssignableFrom(bean.getClass()))
                            {
                                results.put(beanName, (T) bean);
                            }
                        } catch (final BeansException e)
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
            public Object doInTransaction(final TransactionStatus transactionStatus)
            {
                try
                {
                    return callback.doInTransaction();
                }
                catch (final RuntimeException e)
                {
                    //rollback if something weird happened.
                    transactionStatus.setRollbackOnly();
                    throw e;
                }
            }
        });
    }

    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }

    protected DefaultTransactionDefinition getTransactionDefinition()
    {
        final DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("PluginReadWriteTx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setReadOnly(false);
        return def;
    }



}

