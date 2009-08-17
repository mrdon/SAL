package com.atlassian.sal.bamboo.pluginsettings;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.bamboo.bandana.PlanAwareBandanaContext;

import java.math.BigDecimal;

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

    @Test
    public void testPut()
    {
        assertNull(bambooPluginSettings.put("key", "value"));
        verify(mockBandanaManager).setValue(PlanAwareBandanaContext.GLOBAL_CONTEXT, "key", "value");
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
}
