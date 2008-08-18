package com.atlassian.sal.jira.transaction;

import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.transaction.TransactionCallback;

/**
 * Transaction template that just executes the callback since JIRA doesn't support transactions
 */
public class NoOpTransactionTemplate implements TransactionTemplate
{
    public Object execute(TransactionCallback action)
    {
        return action.doInTransaction();
    }
}
