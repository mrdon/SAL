package com.atlassian.sal.jira.message;

import com.atlassian.cache.CacheFactory;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.jira.util.dbc.Assertions;
import com.atlassian.jira.web.bean.I18nBean;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.sal.core.message.AbstractI18nResolver;

import java.io.Serializable;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * A JIRA I18nResolver.  Uses the user's locale if a user is logged in or the default
 * locale if none can be found.
 */
public class JiraI18nResolver extends AbstractI18nResolver
{
    private final JiraAuthenticationContext jiraAuthenticationContext;
    private final I18nHelper.BeanFactory beanFactory;

    public JiraI18nResolver(final JiraAuthenticationContext jiraAuthenticationContext, final I18nHelper.BeanFactory beanFactory)
    {
        this.jiraAuthenticationContext = jiraAuthenticationContext;
        this.beanFactory = beanFactory;
    }

    public String resolveText(String key, Serializable[] arguments)
    {
        final I18nHelper bean = beanFactory.getInstance(jiraAuthenticationContext.getLocale());
        return bean.getText(key, arguments);
    }

    @Override
    public Map<String, String> getAllTranslationsForPrefix(final String prefix, final Locale locale)
    {
        Assertions.notNull("prefix", prefix);

        //if no locale was provided, fall back to the default locale.
        final I18nBean i18nBean = locale == null ? new I18nBean() : new I18nBean(locale);
        final Collection<ResourceBundle> bundles = i18nBean.getBundles();

        final Map<String, String> ret = new HashMap<String, String>();
        //loop through all resource bundles, and find all keys with the given prefix.
        for (final ResourceBundle bundle : bundles)
        {
            final Enumeration<String> keys = bundle.getKeys();
            while (keys.hasMoreElements())
            {
                final String key = keys.nextElement();
                if (key.startsWith(prefix))
                {
                    ret.put(key, bundle.getString(key));
                }
            }
        }

        return ret;
    }
}
