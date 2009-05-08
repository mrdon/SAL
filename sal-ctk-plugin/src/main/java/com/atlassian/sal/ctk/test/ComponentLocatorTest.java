package com.atlassian.sal.ctk.test;

import java.util.Collection;

import org.springframework.stereotype.Component;

import com.atlassian.plugin.PluginController;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.sal.api.component.ComponentLocator;
import com.atlassian.sal.ctk.CtkTest;
import com.atlassian.sal.ctk.CtkTestResults;
import com.atlassian.sal.spi.HostContextAccessor;

@Component
public class ComponentLocatorTest implements CtkTest
{

    public void execute(final CtkTestResults results)
    {
        try
        {
            final PluginController mgr = ComponentLocator.getComponent(PluginController.class);
            results.assertTrue("PluginController accessible in ComponentLocator", mgr != null);

            final Collection<PluginController> c = ComponentLocator.getComponents(PluginController.class);
            results.assertTrue("Should be one PluginController found", c != null && !c.isEmpty());
            results.assertTrue("There should be only one PluginController", c.size()==1);

            final PluginAccessor accessor = ComponentLocator.getComponent(PluginAccessor.class);
            results.assertTrue("PluginAccessor accessible in ComponentLocator", accessor != null);

            final Collection<PluginAccessor> ca = ComponentLocator.getComponents(PluginAccessor.class);
            results.assertTrue("Should be one PluginAccessor found", ca != null && !ca.isEmpty());
            results.assertTrue("There should be only one PluginAccessor", ca.size()==1);

            results.assertTrueOrWarn("PluginAccessor should be accessible in ComponentLocator", ComponentLocator.getComponent(PluginAccessor.class) != null);
        } catch (final UnsupportedOperationException ex)
        {
            results.fail("ComponentLocator operations should be supported");
            ex.printStackTrace();
        }
    }
}
