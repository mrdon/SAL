package com.atlassian.sal.ctk.test;

import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.sal.ctk.CtkTest;
import com.atlassian.sal.ctk.CtkTestResults;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Set;

@Component
public class LocaleResolverTest implements CtkTest
{
    private final LocaleResolver localeResolver;

    public LocaleResolverTest(LocaleResolver localeResolver)
    {
        this.localeResolver = localeResolver;
    }

    public void execute(final CtkTestResults results) throws Exception
    {
        results.assertTrue("LocaleResolver should be injectable", localeResolver != null);
        
        final Set<Locale> localeSet = localeResolver.getSupportedLocales();
        results.assertTrue("LocaleResolver should return at least one supported locale", localeSet.size() >= 1);
    }
}
