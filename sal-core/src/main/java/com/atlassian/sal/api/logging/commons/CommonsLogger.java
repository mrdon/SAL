/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.atlassian.sal.api.logging.commons;

import org.apache.commons.logging.Log;

import com.atlassian.sal.api.logging.Logger;
import com.atlassian.sal.api.logging.LoggerUtils;

/**
 * Simple logger that delegates to commons logging
 */
public class CommonsLogger implements Logger {
    
    private Log log;
    
    public CommonsLogger(Log log) {
        this.log = log;
    }

    public void error(String msg, String... args) {
        log.error(LoggerUtils.format(msg, args));
    }

    public void error(String msg, Throwable ex, String... args) {
        log.error(LoggerUtils.format(msg, args), ex);
    }

    public void info(String msg, String... args) {
        log.info(LoggerUtils.format(msg, args));
    }

    public void info(String msg, Throwable ex, String... args) {
        log.info(LoggerUtils.format(msg, args), ex);
    }

    

    public boolean isInfoEnabled() {
        return log.isInfoEnabled();
    }

    public void warn(String msg, String... args) {
        log.warn(LoggerUtils.format(msg, args));
    }

    public void warn(String msg, Throwable ex, String... args) {
        log.warn(LoggerUtils.format(msg, args), ex);
    }
    
    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }
    
    public void debug(String msg, String... args) {
        log.debug(LoggerUtils.format(msg, args));
    }

    public void debug(String msg, Throwable ex, String... args) {
        log.debug(LoggerUtils.format(msg, args), ex);
    }
    
    public boolean isTraceEnabled() {
        return log.isTraceEnabled();
    }
    
    public void trace(String msg, String... args) {
        log.trace(LoggerUtils.format(msg, args));
    }

    public void trace(String msg, Throwable ex, String... args) {
        log.trace(LoggerUtils.format(msg, args), ex);
    }


    public void fatal(String msg, String... args) {
        log.fatal(LoggerUtils.format(msg, args));
    }

    public void fatal(String msg, Throwable ex, String... args) {
        log.fatal(LoggerUtils.format(msg, args), ex);
    }

    public boolean isErrorEnabled() {
        return log.isErrorEnabled();
    }

    public boolean isFatalEnabled() {
        return log.isFatalEnabled();
    }

    public boolean isWarnEnabled() {
        return log.isWarnEnabled();
    }

}
