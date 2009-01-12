package com.atlassian.sal.refimpl.message;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import com.atlassian.sal.api.message.LocaleResolver;

public class RefimplLocaleResolver implements LocaleResolver
{
    public Locale getLocale(HttpServletRequest request)
    {
        return request.getLocale();
    }
}
