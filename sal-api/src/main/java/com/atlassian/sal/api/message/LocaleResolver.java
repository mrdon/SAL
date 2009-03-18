package com.atlassian.sal.api.message;

import java.util.Locale;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

/**
 * This interface is responsible for resolving the current locale.
 */
public interface LocaleResolver
{
    /**
     * Given a request, resolve the {@link Locale} that should be used in internationalization and localization.
     * The locale should be determined by first checking the remote users preferences, then checking the preferred 
     * locale as specified in the request and finally defaulting to the system locale if no preferred locale is set.  
     * 
     * @param request Request to check 
     * @return Locale to be used in i18n and l10n
     */
    Locale getLocale(HttpServletRequest request);

    /**
     * Returns a set of all the supported locales by the host application. This is all the language packs that
     * are installed.
     *
     * @return a set of all the supported locales by the host application.
     */
    Set<Locale> getSupportedLocales();
}
