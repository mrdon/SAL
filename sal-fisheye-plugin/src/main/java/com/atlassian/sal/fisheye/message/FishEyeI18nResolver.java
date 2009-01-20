package com.atlassian.sal.fisheye.message;

import java.io.Serializable;
import java.util.*;
import java.text.MessageFormat;

import com.atlassian.sal.core.message.AbstractI18nResolver;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.elements.ResourceDescriptor;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.events.PluginDisabledEvent;
import com.atlassian.plugin.event.events.PluginEnabledEvent;

/**
 * This is a copy of the Refimpl resolver, and is in place here until FishEye realises that not everyone speaks English
 */
public class FishEyeI18nResolver extends AbstractI18nResolver
{
    private final Map<String, Iterable<ResourceBundle>> pluginResourceBundles =
        new HashMap<String, Iterable<ResourceBundle>>();

    public FishEyeI18nResolver(PluginAccessor pluginAccessor, PluginEventManager pluginEventManager)
    {
        pluginEventManager.register(this);
        addPluginResourceBundles(pluginAccessor.getPlugins());
    }

    public String resolveText(String key, Serializable[] arguments)
    {
        String message = null;
        for (Iterable<ResourceBundle> bundles : pluginResourceBundles.values())
        {
            for (ResourceBundle bundle : bundles)
            {
                try
                {
                    message = MessageFormat.format(bundle.getString(key), (Object[]) arguments);
                }
                catch (MissingResourceException e)
                {
                    // ignore, try next bundle
                }
            }
        }
        if (message == null)
        {
            message = key;
        }
        return message;
    }

    @PluginEventListener
    public void pluginEnabled(PluginEnabledEvent event)
    {
        addPluginResourceBundles(event.getPlugin());
    }

    @PluginEventListener
    public void pluginDisabled(PluginDisabledEvent event)
    {
        removePluginResourceBundles(event.getPlugin());
    }

    private void addPluginResourceBundles(Iterable<Plugin> plugins)
    {
        for (Plugin plugin : plugins)
        {
            addPluginResourceBundles(plugin);
        }
    }

    private void addPluginResourceBundles(Plugin plugin)
    {
        List<ResourceBundle> bundles = new LinkedList<ResourceBundle>();
        Iterable<ResourceDescriptor> descriptors = plugin.getResourceDescriptors("i18n");
        for (ResourceDescriptor descriptor : descriptors)
        {
            try
            {
                bundles.add(ResourceBundle.getBundle(descriptor.getLocation(), Locale.getDefault(),
                    plugin.getClassLoader()));
            }
            catch (MissingResourceException e)
            {
                // ignore, move on to next one
            }
        }
        pluginResourceBundles.put(plugin.getKey(), bundles);
    }

    private void removePluginResourceBundles(Plugin plugin)
    {
        pluginResourceBundles.remove(plugin.getKey());
    }

}
