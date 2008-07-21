package com.atlassian.sal.ctk.test;

import com.atlassian.sal.ctk.CtkTest;
import com.atlassian.sal.ctk.CtkTestResults;
import com.atlassian.sal.api.component.ComponentLocator;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.project.ProjectManager;
import com.atlassian.sal.api.search.SearchProvider;
import com.atlassian.sal.api.search.SearchResults;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.plugin.PluginManager;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class TransactionTemplateTest implements CtkTest
{
    private final TransactionTemplate template;
    private boolean called = false;

    public TransactionTemplateTest(TransactionTemplate template) {this.template = template;}

    public void execute(CtkTestResults results)
    {
        results.assertTrue("TransactionTemplate should be injectable", template != null);

        String result = (String) template.execute(new TransactionCallback()
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