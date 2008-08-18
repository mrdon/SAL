package com.atlassian.sal.spi;

import java.util.Map;

/**
 * Interface for accessing host information, meant to be exposed as a host component
 */
public interface HostContextAccessor
{
    /**
     * Gets all beans of a given type
     * @param iface The interface to use
     * @return A map of String keys and object instances
     */
    <T> Map<String, T> getComponentsOfType(Class<T> iface);

    /**
     * Runs an action in a transaction and returns a optional value.
     * @param callback The callback class to execute
     * @return Optional result of the operation. May be null
     * @throws RuntimeException if anything went wrong.  The caller will be responsible for rolling back.
     */
    Object doInTransaction(HostTransactionCallback callback);

    /**
     * The interface to implement for code that needs to be ran inside a host transaction
     */
    public static interface HostTransactionCallback
    {
        /**
         * Runs an action in a transaction and returns a optional value.
         * @return Optional result of the operation. May be null
         * @throws RuntimeException if anything went wrong.  The caller will be responsible for rolling back.
         */
        Object doInTransaction();
    }
}
