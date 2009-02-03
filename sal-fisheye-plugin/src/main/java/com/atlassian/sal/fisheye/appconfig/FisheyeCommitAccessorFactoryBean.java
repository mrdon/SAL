package com.atlassian.sal.fisheye.appconfig;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.springframework.beans.factory.FactoryBean;

import com.cenqua.fisheye.AppConfig;

public class FisheyeCommitAccessorFactoryBean implements FactoryBean
{
    private static final ClassLoader FISHEYE_HOST_CLASSLOADER = AppConfig.class.getClassLoader();

    public Object getObject() throws Exception
    {
        return wrapService(new Class[]{FisheyeCommitAccessor.class}, new DefaultFisheyeCommitAccessor(), FISHEYE_HOST_CLASSLOADER);
    }

    public Class<?> getObjectType()
    {
        return FisheyeCommitAccessor.class;
    }

    public boolean isSingleton()
    {
        return true;
    }

    /**
     * Copied from DefaultComponentRegistrar.java
     * Wraps the service in a dynamic proxy that ensures all methods are executed with the passed class loader
     * as the context class loader
     * @param interfaces The interfaces to proxy
     * @param service The instance to proxy
     * @param contextClassLoader
     * @return A proxy that wraps the service
     */
    private Object wrapService(final Class<?>[] interfaces, final Object service, final ClassLoader contextClassLoader)
    {
        return Proxy.newProxyInstance(getClass().getClassLoader(), interfaces, new InvocationHandler()
        {
            public Object invoke(final Object o, final Method method, final Object[] objects) throws Throwable
            {
                final Thread thread = Thread.currentThread();
                final ClassLoader originalContextClassLoader = thread.getContextClassLoader();
                try
                {
                    thread.setContextClassLoader(contextClassLoader);
                    return method.invoke(service, objects);
                } catch (final InvocationTargetException e)
                {
                    throw e.getTargetException();
                } finally
                {
                    thread.setContextClassLoader(originalContextClassLoader);
                }
            }
        });
    }

}
