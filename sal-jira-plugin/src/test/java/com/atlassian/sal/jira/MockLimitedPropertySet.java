package com.atlassian.sal.jira;

import java.util.HashMap;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.map.MapPropertySet;


/**
 * Limits strings to 255.
 */
public class MockLimitedPropertySet extends MapPropertySet
{
    
    public MockLimitedPropertySet()
    {
        setMap(new HashMap());
    }

    @Override
    public void setString(String key, String value)
    {
        if (value.length() > 255)
            throw new IllegalArgumentException("String is too long");
        super.setString(key, value);
    }
    
    @Override
    public int getType(String key) throws UnsupportedOperationException
    {
        return PropertySet.STRING;
    }
    
}
