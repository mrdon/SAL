package com.atlassian.sal.bamboo.pluginsettings;

import java.math.BigDecimal;

import com.atlassian.bamboo.bandana.PlanAwareBandanaContext;
import com.atlassian.bandana.BandanaManager;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings({"JUnitTestMethodWithNoAssertions", "UnusedDeclaration"})
public class TestBambooPluginSettings
{
    private BambooPluginSettings bambooPluginSettings;
    @Mock
    private BandanaManager mockBandanaManager;

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
        bambooPluginSettings = new BambooPluginSettings(mockBandanaManager, PlanAwareBandanaContext.GLOBAL_CONTEXT);
    }

    @Test
    public void testGet()
    {
        bambooPluginSettings.get("key");
        verify(mockBandanaManager).getValue(PlanAwareBandanaContext.GLOBAL_CONTEXT, "key", false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetWithNullKey()
    {
        bambooPluginSettings.get(null);
    }

    @Test
    public void getAcceptsAnyKeyLength()
    {
        bambooPluginSettings.get(StringUtils.repeat("a", 101));
        bambooPluginSettings.get(StringUtils.repeat("a", 255));
        bambooPluginSettings.get(StringUtils.repeat("a", 256));
    }

    @Test
    public void testPut()
    {
        assertNull(bambooPluginSettings.put("key", "value"));
        verify(mockBandanaManager).setValue(PlanAwareBandanaContext.GLOBAL_CONTEXT, "key", "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutWithNullKey()
    {
        bambooPluginSettings.put(null, "foo");
    }

    @Test
    public void putWithLongKeysSucceed()
    {
        bambooPluginSettings.put(StringUtils.repeat("a", 101), "foo");
        bambooPluginSettings.put(StringUtils.repeat("a", 255), "foo");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void putWithVeryLongKeyFails()
    {
        bambooPluginSettings.put(StringUtils.repeat("a", 256), "foo");
    }

    @Test
    public void testPutRemoves()
    {
        when(mockBandanaManager.getValue(PlanAwareBandanaContext.GLOBAL_CONTEXT, "key", false)).thenReturn("old");
        assertEquals("old", bambooPluginSettings.put("key", "value"));
        verify(mockBandanaManager).setValue(PlanAwareBandanaContext.GLOBAL_CONTEXT, "key", "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutUnsupported()
    {
        bambooPluginSettings.put("key", BigDecimal.ONE);
    }

    @Test
    public void testRemove()
    {
        when(mockBandanaManager.getValue(PlanAwareBandanaContext.GLOBAL_CONTEXT, "key", false)).thenReturn("old");
        assertEquals("old", bambooPluginSettings.remove("key"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveWithNullKey()
    {
        bambooPluginSettings.remove(null);
    }

    @Test
    public void removeAcceptsAnyKeyLength()
    {
        bambooPluginSettings.remove(StringUtils.repeat("a", 101));
        bambooPluginSettings.remove(StringUtils.repeat("a", 255));
        bambooPluginSettings.remove(StringUtils.repeat("a", 256));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void putWithLongKeyFailsInDevMode()
    {
        String previousValue = System.setProperty("atlassian.dev.mode", "true");
        try
        {
            // A new instance to pick up the system property
            bambooPluginSettings = new BambooPluginSettings(mockBandanaManager, PlanAwareBandanaContext.GLOBAL_CONTEXT);
            bambooPluginSettings.put(StringUtils.repeat("a", 101), "foo");
        }
        finally
        {
            System.setProperty("atlassian.dev.mode", StringUtils.defaultString(previousValue));
        }
    }
}
