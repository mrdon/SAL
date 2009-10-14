package com.atlassian.sal.jira;

import com.atlassian.sal.jira.pluginsettings.JiraPluginSettings;
import com.atlassian.sal.jira.pluginsettings.UnlimitedStringsPropertySet;
import com.mockobjects.constraint.Constraint;
import com.mockobjects.dynamic.C;
import com.mockobjects.dynamic.Mock;
import com.opensymphony.module.propertyset.PropertySet;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;


public class TestJiraPluginSettings extends TestCase
{
    Mock mockPropertySet;
    JiraPluginSettings map;

    public void setUp()
    {
        mockPropertySet = new Mock(PropertySet.class);
        map = new JiraPluginSettings((PropertySet) mockPropertySet.proxy());
    }

    public void testPropertySetString()
    {
        mockPropertySet.expectAndReturn("exists", C.args(C.eq("foo")), false);
        mockPropertySet.expectAndReturn("exists", C.args(C.eq("foo")), false);
        mockPropertySet.expect("setString", C.args(C.eq("foo"), C.eq("bar")));
        map.put("foo", "bar");
        mockPropertySet.verify();
    }
    public void testPropertySetVeryLongString()
    {
        PropertySet mockMapPropertySet = new MockLimitedPropertySet();
        PropertySet wrappedPropertySet = UnlimitedStringsPropertySet.create(mockMapPropertySet);
        JiraPluginSettings jiraPluginSettings = new JiraPluginSettings(wrappedPropertySet);

        String veryLongString = "thiiiiiiiiiiiiiisIIIIIIIIIIIIIsssssssVEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEERYLOOOOOOOOOOOOOOOOOOOOOOOOOOOOOONGSTRIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIINGbaaaaaaaaaaar";
        jiraPluginSettings.put("foo", veryLongString);
        assertEquals(veryLongString, jiraPluginSettings.get("foo"));
    }

    public void testPropertySetList()
    {
        mockPropertySet.expectAndReturn("exists", C.args(C.eq("foo")), false);
        mockPropertySet.expectAndReturn("exists", C.args(C.eq("foo")), false);
        mockPropertySet.expect("setString", C.args(C.eq("foo"), C.eq("#java.util.List\nbar\njim")));
        map.put("foo", Arrays.asList(new String[]{"bar", "jim"}));
        mockPropertySet.verify();
    }

    public void testPropertyGetList()
    {
        mockPropertySet.expectAndReturn("exists", C.args(C.eq("foo")), true);
        mockPropertySet.expectAndReturn("getType", C.args(C.eq("foo")), PropertySet.STRING);
        mockPropertySet.expectAndReturn("getString", C.args(C.eq("foo")), "#java.util.List\nbar\njim");
        List list = (List) map.get("foo");
        assertNotNull(list);
        assertEquals(2, list.size());
        assertEquals("bar", list.get(0));
        assertEquals("jim", list.get(1));
        mockPropertySet.verify();
    }

    public void testPropertySetProperties()
    {
        mockPropertySet.expectAndReturn("exists", C.args(C.eq("foo")), false);
        mockPropertySet.expectAndReturn("exists", C.args(C.eq("foo")), false);
        mockPropertySet.expect("setString", C.args(C.eq("foo"), new Constraint() {
            public boolean eval(Object object)
            {
                String val = object.toString();
                return val.startsWith("#java.util.Properties")
                        && val.endsWith("foo=bar"+System.getProperty("line.separator"));
            }
        }));

        Properties props = new Properties();
        props.setProperty("foo", "bar");
        map.put("foo", props);
        mockPropertySet.verify();
    }

    public void testPropertySetNumber()
    {
        mockPropertySet.expectAndReturn("exists", C.args(C.eq("foo")), false);
        try
        {
            map.put("foo", new Integer(1));
            fail("Should have rejected unknown object");
        } catch(Exception ex)
        {
            // test passed
        }

    }
}
