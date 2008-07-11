package com.atlassian.sal.core.search.parameter;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang.StringUtils;
import com.atlassian.sal.api.search.parameter.SearchParameter;

/**
 * Basic name value pair search parameter.
 */
public class BasicSearchParameter implements SearchParameter
{
    private String name;
    private String value;

    public BasicSearchParameter(String queryString)
    {
        initFromQueryString(queryString);
    }

    public BasicSearchParameter(String name, String value)
    {
        this.name = name;
        this.value = value;
    }

    public String getName()
    {
        return name;
    }

    public String getValue()
    {
        return value;
    }

    public String buildQueryString()
    {
        try
        {
            return URIUtil.encodeWithinQuery(name) + "=" + URIUtil.encodeWithinQuery(value);
        }
        catch (URIException e)
        {
            throw new RuntimeException(e);
        }
    }

    private void initFromQueryString(String queryString)
    {
        if (StringUtils.isEmpty(queryString) || queryString.indexOf("=") == -1)
        {
            throw new IllegalArgumentException("QueryString '" + queryString + "' does not appear to be a valid query string");
        }

        final String[] strings;
        try
        {
            strings = URIUtil.decode(queryString).split("=");
        }
        catch (URIException e)
        {
            throw new RuntimeException(e);
        }
        this.name = strings[0];
        this.value = strings[1];
    }

    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        BasicSearchParameter that = (BasicSearchParameter) o;

        if (!name.equals(that.name))
        {
            return false;
        }
        if (!value.equals(that.value))
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        int result;
        result = name.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }
}
