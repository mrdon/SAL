package com.atlassian.sal.crowd.pluginsettings;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import junit.framework.TestCase;

import org.springframework.dao.DataAccessException;

import com.atlassian.crowd.integration.exception.ObjectNotFoundException;
import com.atlassian.crowd.model.salproperty.SALProperty;
import com.atlassian.crowd.model.salproperty.SALPropertyDAO;

public class CrowdPluginSettingsTest extends TestCase
{
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
	}
	
	public void testPutActual()
	{
		final SALPropertyDAO mockDAO = mock(SALPropertyDAO.class);
		CrowdPluginSettings crowdPluginSettings = new CrowdPluginSettings(null, mockDAO);

		// 3. not null key, not null propertyName, not null value.
		crowdPluginSettings.put("hi", "hello");
		verify(mockDAO).saveOrUpdate(new SALProperty("null","hi","hello"));	// note that SALProperty#equals ignores "hello" anyway

		// 3. not null key, not null propertyName, not null value. 
		crowdPluginSettings = new CrowdPluginSettings("key", mockDAO);
		crowdPluginSettings.put("hi", "hello");
		verify(mockDAO).saveOrUpdate(new SALProperty("key","hi","hello"));	// note that SALProperty#equals ignores "hello" anyway
	}

	public void testGetActual() throws DataAccessException, ObjectNotFoundException
	{
		final SALPropertyDAO mockDAO = mock(SALPropertyDAO.class);
		CrowdPluginSettings crowdPluginSettings = new CrowdPluginSettings(null, mockDAO);
		final SALProperty someSalProperty = new SALProperty(null, null, null);

        // 2. not null key, not null propertyName
		doReturn(someSalProperty).when(mockDAO).find("null","hi");
		crowdPluginSettings.get("hi");
		verify(mockDAO).find("null","hi");
		
		// 3. not null key, not null propertyName, not null value. 
		doReturn(someSalProperty).when(mockDAO).find("key","hi");
		crowdPluginSettings = new CrowdPluginSettings("key", mockDAO);
		crowdPluginSettings.get("hi");
		verify(mockDAO).find("key","hi");
	}

}
