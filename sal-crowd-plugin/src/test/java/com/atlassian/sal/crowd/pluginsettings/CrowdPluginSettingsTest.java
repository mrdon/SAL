package com.atlassian.sal.crowd.pluginsettings;

import com.atlassian.crowd.exception.ObjectNotFoundException;
import com.atlassian.crowd.manager.property.PluginPropertyManager;
import junit.framework.TestCase;
import static org.mockito.Mockito.*;
import org.springframework.dao.DataAccessException;

public class CrowdPluginSettingsTest extends TestCase
{
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    public void testPutActual()
    {
        final PluginPropertyManager mockPluginPropertyManager = mock(PluginPropertyManager.class);
        CrowdPluginSettings crowdPluginSettings = new CrowdPluginSettings(null, mockPluginPropertyManager);

        // 3. not null key, not null propertyName, not null value.
        crowdPluginSettings.put("hi", "hello");
        verify(mockPluginPropertyManager).setProperty("null", "hi", "hello");    // note that SALProperty#equals ignores "hello" anyway

        // 3. not null key, not null propertyName, not null value.
        crowdPluginSettings = new CrowdPluginSettings("key", mockPluginPropertyManager);
        crowdPluginSettings.put("hi", "hello");
        verify(mockPluginPropertyManager).setProperty("key", "hi", "hello");    // note that SALProperty#equals ignores "hello" anyway
    }

    public void testGetActual() throws DataAccessException, ObjectNotFoundException
    {
        final PluginPropertyManager mockPluginPropertyManager = mock(PluginPropertyManager.class);
        CrowdPluginSettings crowdPluginSettings = new CrowdPluginSettings(null, mockPluginPropertyManager);

        // 2. not null key, not null propertyName
        doReturn("random property").when(mockPluginPropertyManager).getProperty("null", "hi");
        crowdPluginSettings.get("hi");
        verify(mockPluginPropertyManager).getProperty("null", "hi");

        // 3. not null key, not null propertyName, not null value.
        doReturn("random property").when(mockPluginPropertyManager).getProperty("key", "hi");
        crowdPluginSettings = new CrowdPluginSettings("key", mockPluginPropertyManager);
        crowdPluginSettings.get("hi");
        verify(mockPluginPropertyManager).getProperty("key", "hi");
    }

}
