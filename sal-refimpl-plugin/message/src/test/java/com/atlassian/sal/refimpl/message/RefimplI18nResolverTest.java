package com.atlassian.sal.refimpl.message;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.PluginEventManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import static org.mockito.Mockito.mock;

public class RefimplI18nResolverTest
{
    private static final String PLUGIN_KEY = "plugin.key";
    
    private static final String PREFIX = "com.atlassian.sal.message.test.";

    private static final String KEY_WITH_PREFIX_AT_START_1 = PREFIX + "key1";
    private static final String VALUE_FOR_KEY_WITH_PREFIX_AT_START_1 = "Hello, world";

    private static final String KEY_WITH_PREFIX_AT_START_2 = PREFIX + "key2";
    private static final String VALUE_FOR_KEY_WITH_PREFIX_AT_START_2 = "Hello, everyone";

    private static final ResourceBundle bundleWithMatch1 = new ListResourceBundle()
    {
        @Override public Locale getLocale()
        {
            return Locale.US;
        }

        protected Object[][] getContents()
        {
            return new Object[][]
                {
                    {KEY_WITH_PREFIX_AT_START_1, VALUE_FOR_KEY_WITH_PREFIX_AT_START_1},
                    {"noprefix.key", "Hello, Atlassian"},
                    {"notatstart." + PREFIX + "key", "Hello, San Francisco"}
                };
        }
    };
    private static final ResourceBundle bundleWithMatch2 = new ListResourceBundle()
    {
        @Override public Locale getLocale()
        {
            return Locale.US;
        }

        protected Object[][] getContents()
        {
            return new Object[][]
                {
                    {"another.noprefix.key", "Hello, universe"},
                    {"another.notatstart." + PREFIX + "key3", "Hello, Sydney"},
                    {KEY_WITH_PREFIX_AT_START_2, VALUE_FOR_KEY_WITH_PREFIX_AT_START_2}
                };
        }
    };

    PluginAccessor pluginAccessor = mock(PluginAccessor.class);

    PluginEventManager pluginEventManager = mock(PluginEventManager.class);
    private RefimplI18nResolver resolver = new RefimplI18nResolver(pluginAccessor, pluginEventManager);

    @Test
    public void getAllTranslationsForPrefixWithNoResourceBundlesAvailableReturnsEmptyMap()
    {
        assertTrue(resolver.getAllTranslationsForPrefix(PREFIX, Locale.US).isEmpty());
    }

    @Test
    public void getAllTranslationsForPrefixWithMatchingPrefixAndLocaleReturnsMatches()
    {
        resolver.addPluginResourceBundles(PLUGIN_KEY, Collections.singletonList(bundleWithMatch1));

        Map<String, String> matches = resolver.getAllTranslationsForPrefix(PREFIX, Locale.US);
        assertEquals(1, matches.size());
        String key = matches.keySet().iterator().next();
        assertEquals(KEY_WITH_PREFIX_AT_START_1, key);
        assertEquals(VALUE_FOR_KEY_WITH_PREFIX_AT_START_1, matches.get(key));
    }

    @Test
    public void getAllTranslationsForPrefixWithMatchingPrefixAndNonMatchingLocaleReturnsEmptyMap()
    {
        resolver.addPluginResourceBundles(PLUGIN_KEY, Collections.singletonList(bundleWithMatch1));

        assertTrue(resolver.getAllTranslationsForPrefix(PREFIX, Locale.FRANCE).isEmpty());        
    }

    @Test
    public void getAllTranslationsForPrefixWithNonMatchingPrefixAndMatchingLocaleReturnsEmptyMap()
    {
        resolver.addPluginResourceBundles(PLUGIN_KEY, Collections.singletonList(bundleWithMatch1));

        assertTrue(resolver.getAllTranslationsForPrefix(PREFIX + "nomatch", Locale.US).isEmpty());        
    }

    @Test
    public void getAllTranslationsForPrefixWithMultipleMatchingBundlesInOnePluginReturnsAllMatches()
    {
        resolver.addPluginResourceBundles(PLUGIN_KEY, Arrays.asList(bundleWithMatch1, bundleWithMatch2));

        Map<String, String> expectations = new HashMap<String, String>();
        expectations.put(KEY_WITH_PREFIX_AT_START_1, VALUE_FOR_KEY_WITH_PREFIX_AT_START_1);
        expectations.put(KEY_WITH_PREFIX_AT_START_2, VALUE_FOR_KEY_WITH_PREFIX_AT_START_2);

        Map<String, String> matches = resolver.getAllTranslationsForPrefix(PREFIX, Locale.US);
        assertEquals(expectations, matches);
    }

    @Test
    public void getAllTranslationsForPrefixWithMultipleMatchingBundlesInDifferentPluginsReturnsAllMatches()
    {
        resolver.addPluginResourceBundles(PLUGIN_KEY, Collections.singletonList(bundleWithMatch1));
        resolver.addPluginResourceBundles("other." + PLUGIN_KEY, Collections.singletonList(bundleWithMatch2));

        Map<String, String> expectations = new HashMap<String, String>();
        expectations.put(KEY_WITH_PREFIX_AT_START_1, VALUE_FOR_KEY_WITH_PREFIX_AT_START_1);
        expectations.put(KEY_WITH_PREFIX_AT_START_2, VALUE_FOR_KEY_WITH_PREFIX_AT_START_2);

        Map<String, String> matches = resolver.getAllTranslationsForPrefix(PREFIX, Locale.US);
        assertEquals(expectations, matches);
    }
}
