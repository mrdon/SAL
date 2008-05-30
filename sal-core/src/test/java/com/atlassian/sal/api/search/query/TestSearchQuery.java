package com.atlassian.sal.api.search.query;

import junit.framework.TestCase;

import com.atlassian.sal.api.search.parameter.SearchParameter;

/**
 */
public class TestSearchQuery extends TestCase
{
    public void testCreateQuery()
    {
    	SearchQuery query = new DefaultSearchQueryParser().parse("test Query");
    	query.setParameter(SearchParameter.MAXHITS,"12").
    	setParameter("app", "Fisheye");
        assertEquals("test%20Query&maxhits=12&app=Fisheye", query.buildQueryString());
    }

    public void testCreateQueryNoParams()
    {
    	SearchQuery query = new DefaultSearchQueryParser().parse("testQuery");
        assertEquals("testQuery", query.buildQueryString());
    }

    public void testCreateQuerySpecialChars()
    {
        SearchQuery query = new DefaultSearchQueryParser().parse("test%^#Query");
        query.setParameter(SearchParameter.MAXHITS, "12").
                setParameter("ap!@#p", "Fis%^#heye");
        assertEquals("test%25%5E%23Query&maxhits=12&ap!%40%23p=Fis%25%5E%23heye", query.buildQueryString());        
    }
    
    public void testAppendCreateQuerySpecialChars()
    {
    	SearchQuery query = new DefaultSearchQueryParser().parse("test Query&maxhits=12&app=Fisheye&a=b");
    	query.setParameter(SearchParameter.MAXHITS, "10").
    	setParameter("a", "c").append(" and some more&app=Crucible");
    	assertEquals("test%20Query%20and%20some%20more&maxhits=10&app=Crucible&a=c", query.buildQueryString());        
    }
    

    public void testParseComplexQuery()
    {
        SearchQuery query = new DefaultSearchQueryParser().parse("simpleQuery&maxhits=12&app=fisheye&");
        assertEquals("fisheye", query.getParameter("app"));
        assertNull(query.getParameter("nonexistent"));
    }

    public void testParseQuerySpecialCharacters()
    {
        SearchQuery query = new DefaultSearchQueryParser().parse("test%25%5E%23Query&maxhits=12&ap!%40%23p=Fis%25%5E%23heye");
        assertEquals("test%25%5E%23Query", query.getSearchString());
        assertEquals("12", query.getParameter(SearchParameter.MAXHITS));
        assertEquals("Fis%^#heye", query.getParameter("ap!@#p"));
    }
    
}
