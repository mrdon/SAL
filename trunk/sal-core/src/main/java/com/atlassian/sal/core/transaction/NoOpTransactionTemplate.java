package com.atlassian.sal.core.transaction;

import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;

/**
 * This provides a default implementation that doesn't actually run the action in a transaction at all.
 * This can be used in applications such as JIRA which don't support transactions.
 */
public class NoOpTransactionTemplate implements TransactionTemplate
{
    public Object execute(TransactionCallback action)
    {
        return action.doInTransaction();
    }
}
