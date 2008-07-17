package com.atlassian.sal.ctk.test;

import com.atlassian.sal.ctk.CtkTest;
import com.atlassian.sal.ctk.CtkTestResults;
import com.atlassian.sal.api.component.ComponentLocator;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.plugin.PluginManager;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class ComponentLocatorTest implements CtkTest
{
    private final List<LifecycleAware> lifecycleAware;

    public ComponentLocatorTest(List<LifecycleAware> lifecycleAware) {this.lifecycleAware = lifecycleAware;}

    public void execute(CtkTestResults results)
    {
        try
        {
            PluginManager mgr = ComponentLocator.getComponent(PluginManager.class);
            results.assertTrue("PluginManager accessible in ComponentLocator", mgr != null);

            Collection c = ComponentLocator.getComponents(PluginManager.class);
            results.assertTrue("Should be one PluginManager found", c != null && c.size() == 1);
        } catch (UnsupportedOperationException ex)
        {
            results.fail("ComponentLocator operations should be supported");
        }
    }
}
