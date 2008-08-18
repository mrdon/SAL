package com.atlassian.sal.ctk.test;

import com.atlassian.sal.ctk.CtkTest;
import com.atlassian.sal.ctk.CtkTestResults;
import com.atlassian.sal.api.component.ComponentLocator;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.spi.HostContextAccessor;
import com.atlassian.plugin.PluginManager;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class ComponentLocatorTest implements CtkTest
{

    public void execute(CtkTestResults results)
    {
        try
        {
            PluginManager mgr = ComponentLocator.getComponent(PluginManager.class);
            results.assertTrue("PluginManager accessible in ComponentLocator", mgr != null);

            Collection c = ComponentLocator.getComponents(PluginManager.class);
            results.assertTrue("Should be one PluginManager found", c != null && c.size() == 1);
            
            results.assertTrueOrWarn("HostContextAccessor should be accessible in ComponentLocator unless the application provides its own ComponentLocator and ", ComponentLocator.getComponent(HostContextAccessor.class) != null);
        } catch (UnsupportedOperationException ex)
        {
            results.fail("ComponentLocator operations should be supported");
        }
    }
}
