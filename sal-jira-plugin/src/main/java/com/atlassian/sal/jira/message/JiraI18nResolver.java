package com.atlassian.sal.jira.message;

import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.util.I18nHelper;
import static com.atlassian.jira.util.dbc.Assertions.notNull;
import com.atlassian.sal.core.message.AbstractI18nResolver;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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

   public Map<String, String> getAllTranslationsForPrefix(final String prefix, final Locale locale)
    {
        notNull("prefix", prefix);
        notNull("locale", locale);

        final I18nHelper i18nBean = beanFactory.getInstance(locale);
        final Set<String> keys = i18nBean.getKeysForPrefix(prefix);

        final Map<String, String> ret = new HashMap<String, String>();
        for (String key : keys)
        {
            ret.put(key, i18nBean.getText(key));
        }

        return Collections.unmodifiableMap(ret);
    }
}
