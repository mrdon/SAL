package com.atlassian.sal.api.search.query;

import com.atlassian.sal.api.util.URIUtil;
import com.atlassian.sal.api.search.parameter.SearchParameter;
import org.apache.commons.lang.StringUtils;

/**
 */
public class DefaultQueryCreator implements QueryCreator
{
    private StringBuffer queryString = new StringBuffer();

    public QueryCreator addQuery(String searchString)
    {
        if (StringUtils.isEmpty(searchString))
        {
            throw new IllegalArgumentException("Search string was empty");
        }
        queryString.insert(0, URIUtil.encodeWithinQuery(searchString));
        return this;
    }

    public QueryCreator append(SearchParameter searchParameter)
    {
        queryString.append(PARAMETER_SEPARATOR).append(searchParameter.getQueryString());
        return this;
    }

    public String queryString()
    {
        return queryString.toString();
    }
}
