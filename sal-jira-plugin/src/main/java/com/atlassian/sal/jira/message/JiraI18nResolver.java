package com.atlassian.sal.jira.message;

import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.web.bean.I18nBean;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.sal.core.message.AbstractI18nResolver;
import org.apache.log4j.Logger;

import java.io.Serializable;

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
}
