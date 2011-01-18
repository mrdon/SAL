package com.atlassian.sal.api.message;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Set;

/**
 * This interface is responsible for resolving the current locale.
 *
 * @since 2.0
 */
public interface LocaleResolver
{
    /**
     * Given a request, resolve the {@link Locale} that should be used in internationalization and localization.
     * The locale should be determined by first checking the remote users preferences, then defaulting to the
     * application default locale if no preferred locale is set.
     *
     * @param request Request to check 
     * @return Locale to be used in i18n and l10n. {@link Locale#getDefault()} if none found.
     */
    Locale getLocale(HttpServletRequest request);

    /**
     * Returns a set of all the supported locales by the host application. This is all the language packs that
     * are installed.
     *
     * @return an unmodifiable set of all the supported locales by the host application. Must contain at least one locale.
     */
    Set<Locale> getSupportedLocales();
}
