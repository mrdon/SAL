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
    private static final String KEY_WITHOUT_PREFIX_1 = "noprefix.key";
    private static final String KEY_WITH_PREFIX_NOT_AT_START_1 = "notatstart." + PREFIX + "key";
    private static final String KEY_WITH_PREFIX_AT_START_ONLY_IN_FRENCH_1 = PREFIX + "onlyinfrench.key";

    private static final String US_VALUE_FOR_KEY_WITH_PREFIX_AT_START_1 = "Hello, world";
    private static final String FR_VALUE_FOR_KEY_WITH_PREFIX_AT_START_1 = "Bonjour tout le monde";
    private static final String US_VALUE_FOR_KEY_WITHOUT_PREFIX_1 = "Hello, Atlassian";
    private static final String FR_VALUE_FOR_KEY_WITHOUT_PREFIX_1 = "Bonjour, Atlassian";
    private static final String US_VALUE_FOR_KEY_WITH_PREFIX_NOT_AT_START_1 = "Hello, San Francisco";
    private static final String FR_VALUE_FOR_KEY_WITH_PREFIX_NOT_AT_START_1 = "Bonjour, San Francisco";
    private static final String FR_VALUE_FOR_KEY_WITH_PREFIX_AT_START_ONLY_IN_FRENCH_1 = "Bonjour, France!";

    private static final String KEY_WITHOUT_PREFIX_2 = "another." + KEY_WITHOUT_PREFIX_1;
    private static final String KEY_WITH_PREFIX_NOT_AT_START_2 = "another.notatstart." + PREFIX + "key";
    private static final String KEY_WITH_PREFIX_AT_START_2 = PREFIX + "key2";

    private static final String US_VALUE_FOR_KEY_WITHOUT_PREFIX_2 = "Hello, universe";
    private static final String US_VALUE_FOR_KEY_WITH_PREFIX_NOT_AT_START_2 = "Hello, Sydney";
    private static final String US_VALUE_FOR_KEY_WITH_PREFIX_AT_START_2 = "Hello, everyone";

    private static final ResourceBundle bundleUS1 = new ListResourceBundle()
    {
        @Override public Locale getLocale()
        {
            return Locale.US;
        }

        protected Object[][] getContents()
        {
            return new Object[][]
                {
                    {KEY_WITH_PREFIX_AT_START_1, US_VALUE_FOR_KEY_WITH_PREFIX_AT_START_1},
                    {KEY_WITHOUT_PREFIX_1, US_VALUE_FOR_KEY_WITHOUT_PREFIX_1},
                    {KEY_WITH_PREFIX_NOT_AT_START_1, US_VALUE_FOR_KEY_WITH_PREFIX_NOT_AT_START_1}
                };
        }
    };
    private static final ResourceBundle bundleUS2 = new ListResourceBundle()
    {
        @Override public Locale getLocale()
        {
            return Locale.US;
        }

        protected Object[][] getContents()
        {
            return new Object[][]
                {
                    {KEY_WITHOUT_PREFIX_2, US_VALUE_FOR_KEY_WITHOUT_PREFIX_2},
                    {KEY_WITH_PREFIX_NOT_AT_START_2, US_VALUE_FOR_KEY_WITH_PREFIX_NOT_AT_START_2},
                    {KEY_WITH_PREFIX_AT_START_2, US_VALUE_FOR_KEY_WITH_PREFIX_AT_START_2}
                };
        }
    };
    private static final ResourceBundle bundleFR = new ListResourceBundle()
    {
        @Override public Locale getLocale()
        {
            return Locale.FRANCE;
        }

        protected Object[][] getContents()
        {
            return new Object[][]
                {
                    {KEY_WITH_PREFIX_AT_START_1, FR_VALUE_FOR_KEY_WITH_PREFIX_AT_START_1},
                    {KEY_WITHOUT_PREFIX_1, FR_VALUE_FOR_KEY_WITHOUT_PREFIX_1},
                    {KEY_WITH_PREFIX_NOT_AT_START_1, FR_VALUE_FOR_KEY_WITH_PREFIX_NOT_AT_START_1},
                    {KEY_WITH_PREFIX_AT_START_ONLY_IN_FRENCH_1, FR_VALUE_FOR_KEY_WITH_PREFIX_AT_START_ONLY_IN_FRENCH_1}
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
        resolver.addPluginResourceBundles(PLUGIN_KEY, Arrays.asList(bundleUS1, bundleFR));

        Map<String, String> expectations = new HashMap<String, String>();
        expectations.put(KEY_WITH_PREFIX_AT_START_1, US_VALUE_FOR_KEY_WITH_PREFIX_AT_START_1);

        assertEquals(expectations, resolver.getAllTranslationsForPrefix(PREFIX, Locale.US));
    }

    @Test
    public void getAllTranslationsForPrefixWithMatchingPrefixAndNonMatchingLocaleReturnsEmptyMap()
    {
        resolver.addPluginResourceBundles(PLUGIN_KEY, Arrays.asList(bundleUS1, bundleFR));

        assertTrue(resolver.getAllTranslationsForPrefix(PREFIX, Locale.GERMANY).isEmpty());
    }

    @Test
    public void getAllTranslationsForPrefixWithNonMatchingPrefixAndMatchingLocaleReturnsEmptyMap()
    {
        resolver.addPluginResourceBundles(PLUGIN_KEY, Arrays.asList(bundleUS1, bundleFR));

        assertTrue(resolver.getAllTranslationsForPrefix(PREFIX + "nomatch", Locale.US).isEmpty());
    }

    @Test
    public void getAllTranslationsForPrefixWithMultipleMatchingBundlesInOnePluginReturnsAllMatches()
    {
        resolver.addPluginResourceBundles(PLUGIN_KEY, Arrays.asList(bundleUS1, bundleFR, bundleUS2));

        Map<String, String> expectations = new HashMap<String, String>();
        expectations.put(KEY_WITH_PREFIX_AT_START_1, US_VALUE_FOR_KEY_WITH_PREFIX_AT_START_1);
        expectations.put(KEY_WITH_PREFIX_AT_START_2, US_VALUE_FOR_KEY_WITH_PREFIX_AT_START_2);

        assertEquals(expectations, resolver.getAllTranslationsForPrefix(PREFIX, Locale.US));
    }

    @Test
    public void getAllTranslationsForPrefixWithMultipleMatchingBundlesInDifferentPluginsReturnsAllMatches()
    {
        resolver.addPluginResourceBundles(PLUGIN_KEY, Arrays.asList(bundleUS1, bundleFR));
        resolver.addPluginResourceBundles("other." + PLUGIN_KEY, Collections.singletonList(bundleUS2));

        Map<String, String> expectations = new HashMap<String, String>();
        expectations.put(KEY_WITH_PREFIX_AT_START_1, US_VALUE_FOR_KEY_WITH_PREFIX_AT_START_1);
        expectations.put(KEY_WITH_PREFIX_AT_START_2, US_VALUE_FOR_KEY_WITH_PREFIX_AT_START_2);

        assertEquals(expectations, resolver.getAllTranslationsForPrefix(PREFIX, Locale.US));
    }

    @Test(expected = NullPointerException.class)
    public void getAllTranslationsWithNullPrefixThrowsNullPointerException()
    {
        resolver.addPluginResourceBundles(PLUGIN_KEY, Arrays.asList(bundleUS1, bundleFR));
        resolver.getAllTranslationsForPrefix(null, Locale.US);
    }

    @Test
    public void getAllTranslationsWithEmptyPrefixReturnsAllTranslationsInMatchingLocales()
    {
        resolver.addPluginResourceBundles(PLUGIN_KEY, Arrays.asList(bundleUS1, bundleFR, bundleUS2));

        Map<String, String> expectations = new HashMap<String, String>();
        expectations.put(KEY_WITH_PREFIX_AT_START_1, US_VALUE_FOR_KEY_WITH_PREFIX_AT_START_1);
        expectations.put(KEY_WITHOUT_PREFIX_1, US_VALUE_FOR_KEY_WITHOUT_PREFIX_1);
        expectations.put(KEY_WITH_PREFIX_NOT_AT_START_1, US_VALUE_FOR_KEY_WITH_PREFIX_NOT_AT_START_1);
        expectations.put(KEY_WITHOUT_PREFIX_2, US_VALUE_FOR_KEY_WITHOUT_PREFIX_2);
        expectations.put(KEY_WITH_PREFIX_NOT_AT_START_2, US_VALUE_FOR_KEY_WITH_PREFIX_NOT_AT_START_2);
        expectations.put(KEY_WITH_PREFIX_AT_START_2, US_VALUE_FOR_KEY_WITH_PREFIX_AT_START_2);

        assertEquals(expectations, resolver.getAllTranslationsForPrefix("", Locale.US));
    }

    @Test(expected = NullPointerException.class)
    public void getAllTranslationsWithNullLocaleThrowsNullPointerException()
    {
        resolver.addPluginResourceBundles(PLUGIN_KEY, Arrays.asList(bundleUS1, bundleFR));
        resolver.getAllTranslationsForPrefix(PREFIX, null);
    }
}
