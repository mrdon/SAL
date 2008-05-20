package com.atlassian.sal.api.message;

import java.io.Serializable;

/**

 */
public abstract class AbstractI18nResolver implements I18nResolver
{
    public String getText(String key, Serializable... arguments)
    {
        Serializable[] resolvedArguments = new Serializable[arguments.length];
        for (int i = 0; i < arguments.length; i++)
        {
            Serializable argument = arguments[i];
            if (argument instanceof Message)
            {
                resolvedArguments[i] = getText((Message) argument);
            }
            else
            {
                resolvedArguments[i] = arguments[i];
            }
        }
        return resolveText(key, resolvedArguments);
    }


    public String getText(String key)
    {
        return getText(key, new Object[0]);
    }

    public String getText(Message message)
    {
        return getText(message.getKey(), message.getArguments());
    }

    public abstract String resolveText(String key, Serializable[] arguments);

}
