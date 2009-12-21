package com.atlassian.sal.bamboo.message;

import org.apache.log4j.Logger;
import com.atlassian.bamboo.utils.i18n.I18nBeanFactory;
import com.atlassian.bamboo.utils.i18n.I18nBean;
import com.atlassian.sal.core.message.AbstractI18nResolver;

import java.io.Serializable;
import java.util.*;

public class BambooI18nResolver extends AbstractI18nResolver
{
    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger log = Logger.getLogger(BambooI18nResolver.class);
    // ------------------------------------------------------------------------------------------------------- Constants
    // ------------------------------------------------------------------------------------------------- Type Properties
    // ---------------------------------------------------------------------------------------------------- Dependencies

    private final I18nBeanFactory i18nBeanFactory;

    // ---------------------------------------------------------------------------------------------------- Constructors

    public BambooI18nResolver(I18nBeanFactory i18nBeanFactory)
    {
        this.i18nBeanFactory = i18nBeanFactory;
    }

    // ----------------------------------------------------------------------------------------------- Interface Methods
    // -------------------------------------------------------------------------------------------------- Action Methods
    // -------------------------------------------------------------------------------------------------- Public Methods
    
    public String resolveText(String key, Serializable[] arguments)
    {
        return getI18nBean().getText(key, arguments);
    }

    public Map<String, String> getAllTranslationsForPrefix(String prefix, Locale locale)
    {
        return getI18nBean(locale).getAllTranslationsForPrefix(prefix);
    }

    // -------------------------------------------------------------------------------------------------- Helper Methods

    private Locale getLocale()
    {
        return Locale.getDefault();
    }

    private I18nBean getI18nBean()
    {
        return getI18nBean(getLocale());
    }

    private I18nBean getI18nBean(Locale locale)
    {
        return i18nBeanFactory.getI18nBean(locale);
    }

    // -------------------------------------------------------------------------------------- Basic Accessors / Mutators
}
