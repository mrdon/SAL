package com.atlassian.sal.api.transaction;

/**
 * A simple callback that needs to be provided with an action to run in the doInTransaction method.
 * It is assumed that if anything goes wrong, doInTransaction will throw a RuntimeException if anything goes
 * wrong, and the calling transactionTemplate will roll back the transaction.
 */
public interface TransactionCallback
{
    /**
     * Runs an action in a transaction and returns a optional value.
     * @return Optional result of the operation. May be null
     * @throws RuntimeException if anything went wrong.  The caller will be responsible for rolling back.
     */
    Object doInTransaction();
}
