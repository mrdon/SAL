package com.atlassian.sal.bamboo.message;

import org.apache.log4j.Logger;
import com.atlassian.sal.core.message.AbstractI18nResolver;
import com.opensymphony.xwork.TextProvider;
import com.opensymphony.xwork.util.LocalizedTextUtil;

import java.io.Serializable;
import java.util.*;

public class BambooI18nResolver extends AbstractI18nResolver
{
    private static final Logger log = Logger.getLogger(BambooI18nResolver.class);
    // ------------------------------------------------------------------------------------------------------- Constants
    private static final String DEFAULT_BUNDLE = "com.atlassian.bamboo.ww2.BambooActionSupport";

    // ------------------------------------------------------------------------------------------------- Type Properties
    // ---------------------------------------------------------------------------------------------------- Dependencies
    private final TextProvider textProvider;

    // ---------------------------------------------------------------------------------------------------- Constructors

    public BambooI18nResolver(TextProvider textProvider)
    {
        this.textProvider = textProvider;
    }

    // ----------------------------------------------------------------------------------------------- Interface Methods
    // -------------------------------------------------------------------------------------------------- Action Methods
    // -------------------------------------------------------------------------------------------------- Public Methods
    public String resolveText(String key, Serializable[] arguments)
    {
        String text = textProvider.getText(key, Arrays.asList(arguments));
        if (text != null)
        {
            return text;
        }
        else
        {
            return key;
        }
    }

    // @TODO This is a crap version of the translator that doesn't allow plugins to plugin their own messages
    public Map<String, String> getAllTranslationsForPrefix(String prefix, Locale locale)
    {
        final Map<String, String> messages = new HashMap<String, String>();
        final ResourceBundle bundle = textProvider.getTexts(DEFAULT_BUNDLE);
        if (bundle != null)
        {
            final Enumeration<String> keys = bundle.getKeys();
            while (keys.hasMoreElements())
            {
                String key = keys.nextElement();
                if (key.startsWith(prefix))
                {
                    messages.put(key, bundle.getString(key));
                }
            }
        }
        else
        {
            // Pretty sure this never happens...
            log.warn("Unable to find bundle " + DEFAULT_BUNDLE + ". No i18n messages will be returned");
        }

        return messages;
    }

    // ------------------------------------------------------------------------------------------------- Helper Methods
    // -------------------------------------------------------------------------------------- Basic Accessors / Mutators
}
