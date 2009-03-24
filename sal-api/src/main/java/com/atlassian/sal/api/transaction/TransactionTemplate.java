package com.atlassian.sal.api.transaction;

/**
 * This allows applications greater control over the transaction in which operations may be executed.
 * This really mimicks {@link org.springframework.transaction.support.TransactionTemplate}, however
 * since JIRA doesn't know about Spring and doesn't support transactions we need to have our own implementation
 * of this interface here.
 *
 * @since 2.0
 */
public interface TransactionTemplate
{
    /**
     * Executes the callback, returning the object returned.  Any runtime exceptions thrown by the callback are assumed
     * to rollback the transaction.
     *
     * @param action The callback
     * @return The object returned from the callback
     */
    Object execute(TransactionCallback action);
}
