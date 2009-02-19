package com.atlassian.sal.confluence.message;

import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.user.User;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

public class ConfluenceLocaleResolver implements LocaleResolver
{
    private final UserAccessor userAccessor;
    private final LocaleManager localeManager;

    public ConfluenceLocaleResolver(LocaleManager localeManager, UserAccessor userAccessor)
    {
        this.localeManager = localeManager;
        this.userAccessor = userAccessor;
    }

    public Locale getLocale(HttpServletRequest request)
    {
        return localeManager.getLocale(getUser(request.getRemoteUser()));
    }

    private User getUser(String username)
    {
        if (username == null)
            return null;
        else
            return userAccessor.getUser(username);
    }
}
