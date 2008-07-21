package com.atlassian.sal.ctk.test;

import com.atlassian.sal.ctk.CtkTest;
import com.atlassian.sal.ctk.CtkTestResults;
import com.atlassian.sal.api.component.ComponentLocator;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.Message;
import com.atlassian.plugin.PluginManager;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class I18nResolverTest implements CtkTest
{
    private final I18nResolver resolver;

    public I18nResolverTest(I18nResolver resolver) {this.resolver = resolver;}


    public void execute(CtkTestResults results)
    {
        results.assertTrue("I18nResolver should be injectable", resolver != null);

        Message msg = resolver.createMessage("key", "arg1");
        results.assertTrue("Should create valid message", msg.getArguments().length == 1 && "key".equals(msg.getKey()));
        results.assertTrue("Should create message collection", resolver.createMessageCollection() != null);

        results.assertTrue("Should return null for key that doesn't exist", resolver.getText("some.key.that.doesnt.exist") == null);
    }
}