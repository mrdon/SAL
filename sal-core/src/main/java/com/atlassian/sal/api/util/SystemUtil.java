package com.atlassian.sal.api.util;

public class SystemUtil
{
    public static final long getLongPropery(String propertyName, long defaultValue)
    {
        try
        {
            return Long.parseLong(System.getProperty(propertyName));
        } catch (NumberFormatException e)
        {
            return defaultValue;
        }
    }

}
