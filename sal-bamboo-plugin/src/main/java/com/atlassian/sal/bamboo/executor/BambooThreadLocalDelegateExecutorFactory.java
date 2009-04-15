package com.atlassian.sal.bamboo.executor;

import com.atlassian.sal.core.executor.DefaultThreadLocalDelegateExecutorFactory;

public class BambooThreadLocalDelegateExecutorFactory extends DefaultThreadLocalDelegateExecutorFactory
{
    // ------------------------------------------------------------------------------------------------------- Constants
    // ------------------------------------------------------------------------------------------------- Type Properties
    // ---------------------------------------------------------------------------------------------------- Dependencies
    // ---------------------------------------------------------------------------------------------------- Constructors
    public BambooThreadLocalDelegateExecutorFactory()
    {
        super(new BambooThreadLocalContextManager());
    }
    // -------------------------------------------------------------------------------------------------- Public Methods
    // ------------------------------------------------------------------------------------------------- Helper Methods
    // -------------------------------------------------------------------------------------- Basic Accessors / Mutators
}
