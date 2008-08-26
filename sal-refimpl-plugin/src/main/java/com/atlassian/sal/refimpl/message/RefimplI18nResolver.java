package com.atlassian.sal.refimpl.message;


import com.atlassian.sal.core.message.AbstractI18nResolver;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Returns the key with args as a string
 */
public class RefimplI18nResolver extends AbstractI18nResolver
{
    public String resolveText(String key, Serializable[] arguments)
    {
        return null;
    }
}
