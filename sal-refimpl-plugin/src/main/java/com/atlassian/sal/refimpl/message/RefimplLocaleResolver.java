package com.atlassian.sal.refimpl.message;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import com.atlassian.sal.api.message.LocaleResolver;

public class RefimplLocaleResolver implements LocaleResolver
{
    public Locale getLocale(HttpServletRequest request)
    {
        String country = request.getParameter("locale.country");
        String lang = request.getParameter("locale.lang");
        String variant = request.getParameter("locale.variant");

        Locale locale;
        if (lang != null && country != null && variant != null)
        {
            locale = new Locale(lang, country, variant);
        }
        else if (lang != null && country != null)
        {
            locale = new Locale(lang, country);
        }
        else if (lang != null)
        {
            locale = new Locale(lang);
        }
        else
        {
            locale = request.getLocale();
        }
        return locale;
    }
}
