package com.atlassian.sal.confluence.message;

import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.sal.core.message.AbstractI18nResolver;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

/**
 */
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
        throw new UnsupportedOperationException("This application does not support this method call yet!");
    }
}
