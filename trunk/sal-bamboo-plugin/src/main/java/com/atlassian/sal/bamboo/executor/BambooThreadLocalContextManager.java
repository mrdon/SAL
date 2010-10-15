package com.atlassian.sal.bamboo.executor;

import org.apache.log4j.Logger;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.context.SecurityContext;
import com.atlassian.sal.core.executor.ThreadLocalContextManager;

public class BambooThreadLocalContextManager implements ThreadLocalContextManager
{
    private static final Logger log = Logger.getLogger(BambooThreadLocalContextManager.class);
    // ------------------------------------------------------------------------------------------------------- Constants
    // ------------------------------------------------------------------------------------------------- Type Properties
    // ---------------------------------------------------------------------------------------------------- Dependencies
    // ---------------------------------------------------------------------------------------------------- Constructors
    // -------------------------------------------------------------------------------------------------- Public Methods

    /**
     * Get the thread local context of the current thread
     *
     * @return The thread local context
     */
    public Object getThreadLocalContext()
    {
        return SecurityContextHolder.getContext();
    }

    /**
     * Set the thread local context on the current thread
     *
     * @param context The context to set
     */
    public void setThreadLocalContext(Object context)
    {
        SecurityContextHolder.setContext((SecurityContext) context);
    }

    /**
     * Clear the thread local context on the current thread
     */
    public void clearThreadLocalContext()
    {
        SecurityContextHolder.clearContext();
    }
    // ------------------------------------------------------------------------------------------------- Helper Methods
    // -------------------------------------------------------------------------------------- Basic Accessors / Mutators
}
