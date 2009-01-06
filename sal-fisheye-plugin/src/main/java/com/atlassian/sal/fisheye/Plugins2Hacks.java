package com.atlassian.sal.fisheye;

import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.cenqua.fisheye.AppConfig;

public class Plugins2Hacks
{
    private static final Logger log = Logger.getLogger(Plugins2Hacks.class);
    public static <V> V doInApplicationContext(final Callable<V> callable)
    {
        final Thread currentThread = Thread.currentThread();
        final ClassLoader originalContextClassLoader = currentThread.getContextClassLoader();
        try
        {
            currentThread.setContextClassLoader(AppConfig.class.getClassLoader());
            return callable.call();
        } catch (final Exception e)
        {
            log.error(e, e);
            return null;
        } finally
        {
            currentThread.setContextClassLoader(originalContextClassLoader);
        }
    }
}
