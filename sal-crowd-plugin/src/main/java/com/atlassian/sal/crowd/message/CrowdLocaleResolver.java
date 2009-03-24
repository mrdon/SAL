package com.atlassian.sal.crowd.message;

import com.atlassian.sal.api.message.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Set;
import java.util.Collections;
import java.util.HashSet;

/**
 * Crowd resolver that only supports the system default locale.
 *
 * @since 2.2.0
 */
public class CrowdLocaleResolver implements LocaleResolver
{
    public Locale getLocale(HttpServletRequest request)
    {
        return Locale.getDefault();
    }

    public Set<Locale> getSupportedLocales()
    {
        return new HashSet<Locale>(Collections.singletonList(Locale.getDefault()));
    }
}
