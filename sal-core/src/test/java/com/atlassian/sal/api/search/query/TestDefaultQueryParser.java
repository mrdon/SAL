package com.atlassian.sal.api.search.query;

import junit.framework.TestCase;

/**
 */
public class TestDefaultQueryParser extends TestCase
{
    public void testParseQuery()
    {
        DefaultQueryParser queryParser = new DefaultQueryParser("simpleQuery");
        assertEquals("simpleQuery", queryParser.getSearchString());
        assertEquals(-1, queryParser.getMaxHits());
        assertEquals(0, queryParser.getParameters().size());
    }

    public void testParseComplexQuery()
    {
        DefaultQueryParser queryParser = new DefaultQueryParser("simpleQuery&maxhits=12&app=fisheye&");
        assertEquals("simpleQuery", queryParser.getSearchString());
        assertEquals(12, queryParser.getMaxHits());
        assertEquals("fisheye", queryParser.getParameterValue("app"));
    }

    public void testParseQuerySpecialCharacters()
    {
        DefaultQueryParser queryParser = new DefaultQueryParser("test%25%5E%23Query&maxhits=12&ap!%40%23p=Fis%25%5E%23heye");
        assertEquals("test%^#Query", queryParser.getSearchString());
        assertEquals(12, queryParser.getMaxHits());
        assertEquals("Fis%^#heye", queryParser.getParameterValue("ap!@#p"));
    }
}
