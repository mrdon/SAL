package com.atlassian.sal.jira.message;

import com.atlassian.jira.web.bean.I18nBean;
import com.atlassian.jira.web.util.LocaleManager;
import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.seraph.auth.DefaultAuthenticator;
import com.opensymphony.user.User;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Resolves the locale for a particular request.  Depends on the user that's currently logged in,
 * otherwise the system default locale will be used. 
 *
 * @since v2.1
 */
public class JiraLocaleResolver implements LocaleResolver
{
    private final LocaleManager localeManager;

    public JiraLocaleResolver(final LocaleManager localeManager)
    {
        this.localeManager = localeManager;
    }

    public Locale getLocale(final HttpServletRequest request)
    {
        final User user = getUser(request);
        //I18nBean will try to use the user's local or fall back to the system default.
        return new I18nBean(user).getLocale();
    }

    public Set<Locale> getSupportedLocales()
    {        
        @SuppressWarnings("unchecked")
        final List<Locale> list = localeManager.getInstalledLocales();

        return Collections.unmodifiableSet(new HashSet<Locale>(list));
    }

    private User getUser(final HttpServletRequest request)
    {
        final HttpSession session = request.getSession(false);
        if(session != null)
        {
            return (User) session.getAttribute(DefaultAuthenticator.LOGGED_IN_KEY);
        }
        return null;
    }
}
