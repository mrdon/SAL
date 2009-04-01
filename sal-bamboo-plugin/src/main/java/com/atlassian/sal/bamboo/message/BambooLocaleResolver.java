package com.atlassian.sal.bamboo.message;

import org.apache.log4j.Logger;
import com.atlassian.sal.api.message.LocaleResolver;
import com.opensymphony.xwork.ActionContext;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Set;

public class BambooLocaleResolver implements LocaleResolver
{
    private static final Logger log = Logger.getLogger(BambooLocaleResolver.class);
    // ------------------------------------------------------------------------------------------------------- Constants
    // ------------------------------------------------------------------------------------------------- Type Properties
    // ---------------------------------------------------------------------------------------------------- Dependencies
    // ---------------------------------------------------------------------------------------------------- Constructors
    // ----------------------------------------------------------------------------------------------- Interface Methods
    // -------------------------------------------------------------------------------------------------- Action Methods
    // -------------------------------------------------------------------------------------------------- Public Methods
    public Locale getLocale(HttpServletRequest httpServletRequest)
    {
        return null;
    }

    public Set<Locale> getSupportedLocales()
    {
        return null;
    }
    // ------------------------------------------------------------------------------------------------- Helper Methods
    // -------------------------------------------------------------------------------------- Basic Accessors / Mutators
}
