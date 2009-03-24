package com.atlassian.sal.jira;

import com.atlassian.sal.jira.pluginsettings.UnlimitedStringsPropertySet;
import com.opensymphony.module.propertyset.PropertySet;
import junit.framework.TestCase;

public class TestUnlimitedStringsPropertySet extends TestCase
{
    private final String STRING_255 = "Aerospace giant Boeing said today it was again delaying the launch of its 787 Dreamliner, with deliveries now set to begin in early 2009. Aerospace giant Boeing said today it was again delaying the launch of its 787 Dreamliner, with deliveries now set to.";

    MockLimitedPropertySet limitedPropertySet;
    PropertySet unlimitedPropertySet;
    @Override
    protected void setUp() throws Exception
    {
        limitedPropertySet = new MockLimitedPropertySet();
        unlimitedPropertySet = UnlimitedStringsPropertySet.create(limitedPropertySet);
        super.setUp();
    }
    
    public void testShortStrings() throws Exception
    {
        final String shortString = "value";
        unlimitedPropertySet.setString("name", shortString);
        assertEquals(shortString, unlimitedPropertySet.getString("name"));
        assertEquals(shortString, limitedPropertySet.getString("name"));
    }

    public void testMediumStrings() throws Exception
    {
        final String stringLessThan255 = "Aerospace giant Boeing said today it was again delaying the launch of its 787 Dreamliner, with deliveries now set to begin in early 2009.";
        unlimitedPropertySet.setString("name", stringLessThan255);
        assertEquals(stringLessThan255, unlimitedPropertySet.getString("name"));
        assertEquals(stringLessThan255, limitedPropertySet.getString("name"));
    }

    public void testString255() throws Exception
    {
        unlimitedPropertySet.setString("name", STRING_255);
        assertEquals(STRING_255, unlimitedPropertySet.getString("name"));
        assertEquals(STRING_255, limitedPropertySet.getString("name"));
    }
    
    public void testString256() throws Exception
    {
        final String string256 = STRING_255+"A";
        unlimitedPropertySet.setString("name", string256);
        assertEquals(string256, unlimitedPropertySet.getString("name"));
        assertEquals("#-#-#2", limitedPropertySet.getString("name"));
        assertEquals(STRING_255, limitedPropertySet.getString("name#-#-#0"));
        assertEquals("A", limitedPropertySet.getString("name#-#-#1"));
    }
    
    public void testString510() throws Exception
    {
        final String string510 = STRING_255+STRING_255;
        unlimitedPropertySet.setString("name", string510);
        assertEquals(string510, unlimitedPropertySet.getString("name"));
        assertEquals("#-#-#2", limitedPropertySet.getString("name"));
        assertEquals(STRING_255, limitedPropertySet.getString("name#-#-#0"));
        assertEquals(STRING_255, limitedPropertySet.getString("name#-#-#1"));
    }

    public void testString511() throws Exception
    {
        final String string511 = STRING_255+STRING_255+"A";
        unlimitedPropertySet.setString("name", string511);
        assertEquals(string511, unlimitedPropertySet.getString("name"));
        assertEquals("#-#-#3", limitedPropertySet.getString("name"));
        assertEquals(STRING_255, limitedPropertySet.getString("name#-#-#0"));
        assertEquals(STRING_255, limitedPropertySet.getString("name#-#-#1"));
        assertEquals("A", limitedPropertySet.getString("name#-#-#2"));
    }

    public void testRemove() throws Exception
    {
        assertFalse(limitedPropertySet.exists("name"));
        limitedPropertySet.setString("name", "value");
        assertTrue(limitedPropertySet.exists("name"));
        limitedPropertySet.remove("name");
        assertFalse(limitedPropertySet.exists("name"));
        
        final String string511 = STRING_255+STRING_255+"A";
        unlimitedPropertySet.setString("name", string511);
        assertEquals(string511, unlimitedPropertySet.getString("name"));
        assertEquals("#-#-#3", limitedPropertySet.getString("name"));
        assertEquals(STRING_255, limitedPropertySet.getString("name#-#-#0"));
        assertEquals(STRING_255, limitedPropertySet.getString("name#-#-#1"));
        assertEquals("A", limitedPropertySet.getString("name#-#-#2"));
        
        unlimitedPropertySet.remove("name");
        assertFalse(unlimitedPropertySet.exists("name"));
        assertFalse(limitedPropertySet.exists("name"));
        assertFalse(limitedPropertySet.exists("name#-#-#0"));
        assertFalse(limitedPropertySet.exists("name#-#-#1"));
        assertFalse(limitedPropertySet.exists("name#-#-#2"));
        assertFalse(limitedPropertySet.exists("name#-#-#3"));
        
    }

}
