package com.atlassian.sal.jira.message;

import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.web.bean.I18nBean;
import com.atlassian.jira.util.dbc.Assertions;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.sal.core.message.AbstractI18nResolver;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.Map;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Collection;
import java.util.HashMap;
import java.util.Enumeration;

/**
 * A JIRA I18nResolver.  Uses the user's locale if a user is logged in or the default
 * locale if none can be found.
 */
public class JiraI18nResolver extends AbstractI18nResolver
{
	private static final Logger log = Logger.getLogger(JiraI18nResolver.class);
    private final JiraAuthenticationContext jiraAuthenticationContext;
    private final PluginAccessor pluginAccessor;

    public JiraI18nResolver(JiraAuthenticationContext jiraAuthenticationContext, PluginAccessor pluginAccessor)
    {
        this.jiraAuthenticationContext = jiraAuthenticationContext;
        this.pluginAccessor = pluginAccessor;
    }

    private I18nBean getI18nBean()
    {
        return new I18nBean(jiraAuthenticationContext.getUser())
        {
            @Override
            protected PluginAccessor getPluginAccessor()
            {
                return pluginAccessor;
            }
        };
    }

    public String resolveText(String key, Serializable[] arguments)
    {
        // TODO: this is creating a new i18n bean for each call. this is suboptimal. need to do some kind of 
        // profiling/benchmarking to see if this is acceptable.
        return getI18nBean().getText(key, arguments);
    }

    @Override
    public Map<String, String> getAllTranslationsForPrefix(final String prefix, final Locale locale)
    {
        Assertions.notNull("prefix", prefix);
        Assertions.notNull("locale", locale);
        
        final Collection<ResourceBundle> bundles = new I18nBean(locale).getBundles();

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
