package com.atlassian.sal.api.search.parameter;

import junit.framework.TestCase;

/**
 */
public class TestBasicSearchParameter extends TestCase
{

    public void testEncodeDecode()
    {
        BasicSearchParameter basicSearchParameter = new BasicSearchParameter("fixfor", "3.12");
        String queryString = basicSearchParameter.getQueryString();
        assertEquals("fixfor=3.12", queryString);

        BasicSearchParameter searchParameter = new BasicSearchParameter(queryString);
        assertEquals("fixfor", searchParameter.getName());
        assertEquals("3.12", searchParameter.getValue());
    }

    public void testEncodeDecodeSpecialCharachters()
    {
        BasicSearchParameter basicSearchParameter = new BasicSearchParameter("fix?for", "3.12!%");
        String queryString = basicSearchParameter.getQueryString();
        assertEquals("fix%3Ffor=3.12!%25", queryString);

        BasicSearchParameter searchParameter = new BasicSearchParameter(queryString);
        assertEquals("fix?for", searchParameter.getName());
        assertEquals("3.12!%", searchParameter.getValue());
    }
}
