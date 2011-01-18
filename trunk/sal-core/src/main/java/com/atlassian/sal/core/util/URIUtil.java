package com.atlassian.sal.core.util;

import org.apache.commons.httpclient.URIException;

/**
 * Wrapper around {@link org.apache.commons.httpclient.util.URIUtil} to avoid silly checked exceptions.
 */
public class URIUtil
{
    public static String encodeWithinQuery(String query)
    {
        try
        {
            return org.apache.commons.httpclient.util.URIUtil.encodeWithinQuery(query);
        }
        catch (URIException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static String decode(String queryString)
    {
        try
        {
            return org.apache.commons.httpclient.util.URIUtil.decode(queryString);
        }
        catch (URIException e)
        {
            throw new RuntimeException(e);
        }
    }
}
