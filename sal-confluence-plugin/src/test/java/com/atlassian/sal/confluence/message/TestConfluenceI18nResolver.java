package com.atlassian.sal.confluence.message;

import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import junit.framework.TestCase;
import org.mockito.Mockito;

import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class TestConfluenceI18nResolver extends TestCase
{
    private static final ResourceBundle EMPTY_RESOURCE_BUNDLE = new ListResourceBundle()
    {
        protected Object[][] getContents()
        {
            return new Object[][] {};
        }
    };

    private ConfluenceI18nResolver confluenceI18nResolver;
    private I18NBean i18nBean;
    private I18NBeanFactory i18NBeanFactory;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        confluenceI18nResolver = new ConfluenceI18nResolver();

        i18nBean = Mockito.mock(I18NBean.class);
        i18NBeanFactory = Mockito.mock(I18NBeanFactory.class);
        confluenceI18nResolver.setI18NBeanFactory(i18NBeanFactory);
    }

    public void testGetAllTranslationsForPrefix()
    {
        ResourceBundle resourceBundle = new ListResourceBundle()
        {
            protected Object[][] getContents()
            {
                return new Object[][] {
                        { "bar", "barValue" },
                        { "foo", "fooValue" },
                        { "foo.suffix", "fooSuffixValue" }
                };
            }
        };
        Mockito.when(i18NBeanFactory.getI18NBean(Locale.FRENCH)).thenReturn(i18nBean);
        Mockito.when(i18nBean.getResourceBundle()).thenReturn(resourceBundle);

        Map<String, String> translations = confluenceI18nResolver.getAllTranslationsForPrefix("foo", Locale.FRENCH);

        assertTrue("translations should contains \"foo\"", translations.containsKey("foo"));
        assertEquals("fooValue", translations.get("foo"));
        assertTrue("translations should contains \"foo.suffix\"", translations.containsKey("foo.suffix"));
        assertEquals("fooSuffixValue", translations.get("foo.suffix"));
        assertFalse(translations.containsKey("bar"));
    }

    public void testNPEOnNullLocale()
    {
        Mockito.when(i18NBeanFactory.getI18NBean(null)).thenReturn(i18nBean);
        Mockito.when(i18nBean.getResourceBundle()).thenReturn(EMPTY_RESOURCE_BUNDLE);

        try
        {
            confluenceI18nResolver.getAllTranslationsForPrefix("foo", null);
            fail(NullPointerException.class.getName() + " expected");
        }
        catch (NullPointerException e)
        {
            // all good
        }
    }

    public void testNPEOnNullPrefix()
    {
        Mockito.when(i18NBeanFactory.getI18NBean(Locale.FRENCH)).thenReturn(i18nBean);
        Mockito.when(i18nBean.getResourceBundle()).thenReturn(EMPTY_RESOURCE_BUNDLE);

        try
        {
            confluenceI18nResolver.getAllTranslationsForPrefix(null, Locale.FRENCH);
            fail(NullPointerException.class.getName() + " expected");
        }
        catch (NullPointerException e)
        {
            // all good
        }
    }
}
