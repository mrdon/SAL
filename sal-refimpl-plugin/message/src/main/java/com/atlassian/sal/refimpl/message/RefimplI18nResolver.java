package com.atlassian.sal.refimpl.message;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Enumeration;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.elements.ResourceDescriptor;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginDisabledEvent;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.atlassian.sal.core.message.AbstractI18nResolver;

/**
 * Returns the key with args as a string
 */
public class RefimplI18nResolver extends AbstractI18nResolver
{
    private final Map<String, Iterable<ResourceBundle>> pluginResourceBundles =
        new HashMap<String, Iterable<ResourceBundle>>();

    public RefimplI18nResolver(PluginAccessor pluginAccessor, PluginEventManager pluginEventManager)
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

    @Override
    public Map<String, String> getAllTranslationsForPrefix(String prefix, Locale locale)
    {
        if (prefix == null)
        {
            throw new NullPointerException("prefix must not be null");
        }
        if (locale == null)
        {
            throw new NullPointerException("locale must not be null");
        }
        Map<String, String> translationsWithPrefix = new HashMap<String, String>();
        for (Iterable<ResourceBundle> bundles : pluginResourceBundles.values())
        {
            addMatchingTranslationsToMap(prefix, locale, bundles, translationsWithPrefix);
        }
        return translationsWithPrefix;
    }

    private void addMatchingTranslationsToMap(String prefix, Locale locale, Iterable<ResourceBundle> bundles,
                                              Map<String, String> translationsWithPrefix)
    {
        for (ResourceBundle bundle : bundles)
        {
            if (bundle.getLocale().equals(locale))
            {
                addMatchingTranslationsToMap(prefix, bundle, translationsWithPrefix);
            }
        }
    }

    private void addMatchingTranslationsToMap(String prefix, ResourceBundle bundle,
                                              Map<String, String> translationsWithPrefix)
    {
        Enumeration enumeration = bundle.getKeys();
        while (enumeration.hasMoreElements())
        {
            String key = (String) enumeration.nextElement();
            if (key.startsWith(prefix))
            {
                translationsWithPrefix.put(key, bundle.getString(key));
            }
        }
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
                bundles.add(ResourceBundle.getBundle(descriptor.getLocation(), Locale.getDefault(), plugin.getClassLoader()));
            }
            catch (MissingResourceException e)
            {
                // ignore, move on to next one
            }
        }
        addPluginResourceBundles(plugin.getKey(), bundles);
    }

    void addPluginResourceBundles(String pluginKey, List<ResourceBundle> bundles)
    {
        pluginResourceBundles.put(pluginKey, bundles);
    }

    private void removePluginResourceBundles(Plugin plugin)
    {
        pluginResourceBundles.remove(plugin.getKey());
    }
}
