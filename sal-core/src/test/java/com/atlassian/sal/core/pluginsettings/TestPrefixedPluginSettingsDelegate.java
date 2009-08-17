package com.atlassian.sal.core.pluginsettings;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.when;
import com.atlassian.sal.api.pluginsettings.PluginSettings;

public class TestPrefixedPluginSettingsDelegate
{
    @Mock
    private PluginSettings target;
    private PluginSettings prefixedPluginSettings;

    private static final Object VALUE = new Object();

    @Before
    public void initMocks()
    {
        MockitoAnnotations.initMocks(this);
        prefixedPluginSettings = new PrefixedPluginSettingsDelegate("prefix", target);
    }

    @Test
    public void testGet()
    {
        when(target.get("prefixkey")).thenReturn(VALUE);
        assertSame(VALUE, prefixedPluginSettings.get("key"));
    }

    @Test
    public void testPut()
    {
        when(target.put("prefixkey", VALUE)).thenReturn(VALUE);
        assertSame(VALUE, prefixedPluginSettings.put("key", VALUE));
    }

    @Test
    public void testRemove()
    {
        when(target.remove("prefixkey")).thenReturn(VALUE);
        assertSame(VALUE, prefixedPluginSettings.remove("key"));        
    }
}
