package com.atlassian.sal.core.message;

import com.atlassian.sal.api.message.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

/**
 * Simple resolver that only supports the system default locale.
 *
 * @since 2.2.0
 */
public class SystemDefaultLocaleResolver implements LocaleResolver
{
    public Locale getLocale(HttpServletRequest request)
    {
        return getLocale();
    }
    
    public Locale getLocale()
    {
    	return Locale.getDefault();
    }

    public Set<Locale> getSupportedLocales()
    {
        return new HashSet<Locale>(Collections.singletonList(Locale.getDefault()));
    }
}
