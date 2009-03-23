package com.atlassian.sal.confluence.message;

import com.atlassian.confluence.languages.Language;
import com.atlassian.confluence.languages.LanguageManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.user.User;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

public class ConfluenceLocaleResolver implements LocaleResolver
{
    private final UserAccessor userAccessor;
    private final LanguageManager languageManager;
    private final LocaleManager localeManager;

    public ConfluenceLocaleResolver(LocaleManager localeManager, UserAccessor userAccessor, LanguageManager languageManager)
    {
        this.localeManager = localeManager;
        this.userAccessor = userAccessor;
        this.languageManager = languageManager;
    }

    public Locale getLocale(HttpServletRequest request)
    {
        return localeManager.getLocale(getUser(request.getRemoteUser()));
    }

    public Set<Locale> getSupportedLocales()
    {
        //TODO: Need to test that this actually works ;)
        final List<Language> langs = languageManager.getLanguages();
        final Set<Locale> ret = new HashSet<Locale>();
        for (Language lang : langs)
        {
            ret.add(lang.getLocale());
        }

        return Collections.unmodifiableSet(ret);
    }

    private User getUser(String username)
    {
        if (username == null)
            return null;
        else
            return userAccessor.getUser(username);
    }
}
