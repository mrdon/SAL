package com.atlassian.sal.confluence.project;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;

import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.SpaceType;

public class TestConfluenceProjectKeyLocator extends TestCase
{
    ConfluenceProjectManager confluenceProjectManager;
    SpaceManager mockSpaceManager;

    @Override
	public void setUp()
    {
        confluenceProjectManager = new ConfluenceProjectManager();
        mockSpaceManager = mock(SpaceManager.class);
		confluenceProjectManager.setSpaceManager(mockSpaceManager);
    }

    public void testGetAllProjectKeys()
    {
        final Space s1 = new Space();
        s1.setKey("s1");
        final Space s2 = new Space();
        s2.setKey("s2");
        final List<Space> spaces = Arrays.asList(s1, s2);
        doReturn(spaces).when(mockSpaceManager).getSpacesByType(SpaceType.GLOBAL);
        final Collection<String> keys = confluenceProjectManager.getAllProjectKeys();
        assertEquals(2, keys.size());
        assertTrue(keys.contains("s1"));
        assertTrue(keys.contains("s2"));
    }
}
