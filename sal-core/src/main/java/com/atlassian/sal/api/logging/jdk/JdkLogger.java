/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.atlassian.sal.api.logging.jdk;

import java.util.logging.Level;

import com.atlassian.sal.api.logging.Logger;
import com.atlassian.sal.api.logging.LoggerUtils;

/**
 * Delegates to jdk logger.  Maps fatal to Level.SEVERE along with error.
 */
public class JdkLogger implements Logger {
    
    private java.util.logging.Logger log;
    
    public JdkLogger(java.util.logging.Logger log) {
        this.log = log;
    }

    public void error(String msg, String... args) {
        log.log(Level.SEVERE, LoggerUtils.format(msg, args));
    }

    public void error(String msg, Throwable ex, String... args) {
        log.log(Level.SEVERE, LoggerUtils.format(msg, args), ex);
    }
    
    public void fatal(String msg, String... args) {
        log.log(Level.SEVERE, LoggerUtils.format(msg, args));
    }

    public void fatal(String msg, Throwable ex, String... args) {
        log.log(Level.SEVERE, LoggerUtils.format(msg, args), ex);
    }

    public void info(String msg, String... args) {
        log.log(Level.INFO, LoggerUtils.format(msg, args));
    }

    public void info(String msg, Throwable ex, String... args) {
        log.log(Level.INFO, LoggerUtils.format(msg, args), ex);
    }

    public boolean isInfoEnabled() {
        return log.isLoggable(Level.INFO);
    }

    public void warn(String msg, String... args) {
        log.log(Level.WARNING, LoggerUtils.format(msg, args));
    }

    public void warn(String msg, Throwable ex, String... args) {
        log.log(Level.WARNING, LoggerUtils.format(msg, args), ex);
    }
    
    public boolean isDebugEnabled() {
        return log.isLoggable(Level.FINE);
    }
    
    public void debug(String msg, String... args) {
        log.log(Level.FINE, LoggerUtils.format(msg, args));
    }

    public void debug(String msg, Throwable ex, String... args) {
        log.log(Level.FINE, LoggerUtils.format(msg, args), ex);
    }
    
    public boolean isTraceEnabled() {
        return log.isLoggable(Level.FINEST);
    }
    
    public void trace(String msg, String... args) {
        log.log(Level.FINEST, LoggerUtils.format(msg, args));
    }

    public void trace(String msg, Throwable ex, String... args) {
        log.log(Level.FINEST, LoggerUtils.format(msg, args), ex);
    }

    public boolean isErrorEnabled() {
        return log.isLoggable(Level.SEVERE);
    }

    public boolean isFatalEnabled() {
        return log.isLoggable(Level.SEVERE);
    }

    public boolean isWarnEnabled() {
        return log.isLoggable(Level.WARNING);
    }

}
