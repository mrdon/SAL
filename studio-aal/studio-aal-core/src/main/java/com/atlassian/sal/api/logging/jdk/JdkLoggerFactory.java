/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.atlassian.sal.api.logging.jdk;

import com.atlassian.sal.api.logging.Logger;
import com.atlassian.sal.api.logging.LoggerFactory;

/**
 * Creates jdk loggers
 */
public class JdkLoggerFactory extends LoggerFactory {

    @Override
    protected Logger getLoggerImpl(Class<?> cls) {
        return new JdkLogger(java.util.logging.Logger.getLogger(cls.getName()));
    }
    
    @Override
    protected Logger getLoggerImpl(String name) {
        return new JdkLogger(java.util.logging.Logger.getLogger(name));
    }
}
