package com.atlassian.sal.api.search.query;

import com.atlassian.sal.api.search.parameter.BasicSearchParameter;
import com.atlassian.sal.api.search.parameter.SearchParameter;
import com.atlassian.sal.api.util.URIUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 */
public class DefaultQueryParser implements QueryParser
{
    private static final Logger log = Logger.getLogger(DefaultQueryParser.class);
    private String searchString;
    private Map<String, SearchParameter> queryParameters = new HashMap<String, SearchParameter>();

    public DefaultQueryParser(String queryString)
    {
        if (StringUtils.isEmpty(queryString))
        {
            throw new IllegalArgumentException("Cannot parse empty query string!");
        }
        parseQueryString(queryString);
    }

    public String getSearchString()
    {
        return searchString;
    }


    public int getMaxHits()
    {
        int maxHits = -1;
        String maxHitsString = getParameterValue(SearchParameter.MAXHITS);
        if (maxHitsString != null)
        {
            try
            {
                maxHits = Integer.parseInt(maxHitsString);
            }
            catch (NumberFormatException e)
            {
                log.error("Invalid " + SearchParameter.MAXHITS + " parameter received: '" + maxHitsString + "'");
            }
        }
        return maxHits;
    }

    public Set<SearchParameter> getParameters()
    {
        return new HashSet<SearchParameter>(queryParameters.values());
    }

    public SearchParameter getParameter(String parameterName)
    {
        return queryParameters.get(parameterName);
    }

    public String getParameterValue(String parameterName)
    {
        if (!queryParameters.containsKey(parameterName))
        {
            return null;
        }
        return queryParameters.get(parameterName).getValue();
    }

    private void parseQueryString(String queryString)
    {
        if (queryString.indexOf(QueryCreator.PARAMETER_SEPARATOR) == -1)
        {
            //looks like there's no params.
            searchString = URIUtil.decode(queryString);
        }
        final String[] strings = queryString.split(QueryCreator.PARAMETER_SEPARATOR);
        searchString = URIUtil.decode(strings[0]);
        for (int i = 1; i < strings.length; i++)
        {
            String string = strings[i];
            BasicSearchParameter searchParam = new BasicSearchParameter(string);
            queryParameters.put(searchParam.getName(), searchParam);
        }
    }
}
