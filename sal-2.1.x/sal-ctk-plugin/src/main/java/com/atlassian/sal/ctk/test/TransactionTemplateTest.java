package com.atlassian.sal.ctk.test;

import org.springframework.stereotype.Component;

import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.ctk.CtkTest;
import com.atlassian.sal.ctk.CtkTestResults;

@Component
public class TransactionTemplateTest implements CtkTest
{
    private final TransactionTemplate template;
    private boolean called = false;

    public TransactionTemplateTest(final TransactionTemplate template)
	{
		this.template = template;
	}

    public void execute(final CtkTestResults results)
    {
        results.assertTrue("TransactionTemplate should be injectable", template != null);

        final String result = (String) template.execute(new TransactionCallback()
        {
            public Object doInTransaction()
            {
                called = true;
                return "hi";
            }
        });

        results.assertTrue("Should have executed callback in a transaction", called);
        results.assertTrue("Should have returned object from callback", "hi".equals(result));
    }
}