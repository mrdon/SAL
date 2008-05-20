package com.atlassian.sal.api.logging;

/**
 * Main logger interface for logging things
 */
public interface Logger {
    void trace(String msg, String... args);
    void trace(String msg, Throwable ex, String... args);
    boolean isTraceEnabled();
    
    void debug(String msg, String... args);
    void debug(String msg, Throwable ex, String... args);
    boolean isDebugEnabled();
    
    void info(String msg, String... args);
    void info(String msg, Throwable ex, String... args);
    boolean isInfoEnabled();
    
    void warn(String msg, String... args);
    void warn(String msg, Throwable ex, String... args);
    boolean isWarnEnabled();
    
    void error(String msg, String... args);
    void error(String msg, Throwable ex, String... args);
    boolean isErrorEnabled();
    
    void fatal(String msg, String... args);
    void fatal(String msg, Throwable ex, String... args);
    boolean isFatalEnabled();
}
