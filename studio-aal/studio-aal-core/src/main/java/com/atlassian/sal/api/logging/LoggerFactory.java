package com.atlassian.sal.api.logging;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.atlassian.sal.api.logging.jdk.JdkLoggerFactory;

/**
 * Creates loggers.  Static accessor will lazily try to decide on the best factory if none specified.
 */
public abstract class LoggerFactory {
    
    private static final ReadWriteLock lock = new ReentrantReadWriteLock();
    private static LoggerFactory factory;
    
    public static void setLoggerFactory(LoggerFactory factory) {
        lock.writeLock().lock();
        try {
            LoggerFactory.factory = factory;
        } finally {
            lock.writeLock().unlock();
        }
            
    }
    
    public static Logger getLogger(Class<?> cls) {
        return getLoggerFactory().getLoggerImpl(cls);
    }
    
    public static Logger getLogger(String name) {
        return getLoggerFactory().getLoggerImpl(name);
    }
    
    protected static LoggerFactory getLoggerFactory() {
        lock.readLock().lock();
        try {
            if (factory != null) {
                return factory;
            }
        } finally {
            lock.readLock().unlock();
        }
        lock.writeLock().lock();
        try {
            if (factory == null) {
                try {
                    Class.forName("org.apache.commons.logging.LogFactory");
                    factory = new com.atlassian.sal.api.logging.commons.CommonsLoggerFactory();
                } catch (ClassNotFoundException ex) {
                    // commons logging not found, falling back to jdk logging
                    factory = new JdkLoggerFactory();
                }
            }
            return factory;
        }
        finally {
            lock.writeLock().unlock();
        }
    }
    
    protected abstract Logger getLoggerImpl(Class<?> cls);
    
    protected abstract Logger getLoggerImpl(String name); 

}
