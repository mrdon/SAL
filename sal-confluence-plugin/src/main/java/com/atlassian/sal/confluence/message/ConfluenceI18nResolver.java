package com.atlassian.sal.confluence.message;

import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.sal.core.message.AbstractI18nResolver;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class ConfluenceI18nResolver extends AbstractI18nResolver
{
    private I18NBeanFactory i18NBeanFactory;
    private LocaleManager localeManager;

    public String resolveText(String key, Serializable[] arguments)
    {
        return getI18nBean().getText(key, arguments);
    }

    private I18NBean getI18nBean()
    {
        return i18NBeanFactory.getI18NBean(getLocale());
    }

    private Locale getLocale()
    {
        return localeManager.getLocale(AuthenticatedUserThreadLocal.getUser());
    }

    public void setI18NBeanFactory(I18NBeanFactory i18NBeanFactory)
    {
        this.i18NBeanFactory = i18NBeanFactory;
    }

    public void setLocaleManager(LocaleManager localeManager)
    {
        this.localeManager = localeManager;
    }

    public Map<String, String> getAllTranslationsForPrefix(final String prefix, final Locale locale)
    {
        if (prefix == null)
        {
            throw new NullPointerException("prefix must not be null");
        }
        if (locale == null)
        {
            throw new NullPointerException("locale must not be null");
        }

        final Map<String, String> ret = new HashMap<String, String>();
        final I18NBean i18NBean = i18NBeanFactory.getI18NBean(locale);
        final ResourceBundle bundle = i18NBean.getResourceBundle();

        final Enumeration<String> keys = bundle.getKeys();
        while (keys.hasMoreElements())
        {
            final String key = keys.nextElement();
            if (key.startsWith(prefix))
            {
                ret.put(key, bundle.getString(key));
            }
        }

        return ret;
    }

}
