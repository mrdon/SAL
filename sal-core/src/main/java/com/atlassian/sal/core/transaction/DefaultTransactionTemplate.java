package com.atlassian.sal.core.transaction;

import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.spi.HostContextAccessor;

/**
 * This provides a default implementation that delegates to the underlying host context accessor
 */
public class DefaultTransactionTemplate implements TransactionTemplate
{
    private final HostContextAccessor hostContentAccessor;

    public DefaultTransactionTemplate(HostContextAccessor hostContentAccessor)
    {
        this.hostContentAccessor = hostContentAccessor;
    }

    public Object execute(final TransactionCallback action)
    {
        return hostContentAccessor.doInTransaction(new HostContextAccessor.HostTransactionCallback() {

            public Object doInTransaction()
            {
                return action.doInTransaction();
            }
        });
    }
}
