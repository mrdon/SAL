package com.atlassian.sal.api.search.query;

import com.atlassian.sal.api.search.parameter.BasicSearchParameter;
import com.atlassian.sal.api.search.parameter.MaxhitsSearchParameter;
import junit.framework.TestCase;

/**
 */
public class TestDefaultQueryCreator extends TestCase
{
    public void testCreateQuery()
    {
        DefaultQueryCreator queryCreator = new DefaultQueryCreator();
        queryCreator.addQuery("testQuery").
                append(new MaxhitsSearchParameter(12)).
                append(new BasicSearchParameter("app", "Fisheye"));
        assertEquals("testQuery&maxhits=12&app=Fisheye", queryCreator.queryString());
    }

    public void testCreateQueryNoParams()
    {
        DefaultQueryCreator queryCreator = new DefaultQueryCreator();
        queryCreator.addQuery("testQuery");
        assertEquals("testQuery", queryCreator.queryString());
    }

    public void testCreateQuerySpecialChars()
    {
        DefaultQueryCreator queryCreator = new DefaultQueryCreator();
        queryCreator.addQuery("test%^#Query").
                append(new MaxhitsSearchParameter(12)).
                append(new BasicSearchParameter("ap!@#p", "Fis%^#heye"));
        assertEquals("test%25%5E%23Query&maxhits=12&ap!%40%23p=Fis%25%5E%23heye", queryCreator.queryString());        
    }
}
