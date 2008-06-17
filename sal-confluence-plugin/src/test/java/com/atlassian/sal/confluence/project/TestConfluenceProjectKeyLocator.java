package com.atlassian.sal.confluence.project;

import junit.framework.TestCase;
import com.mockobjects.dynamic.Mock;
import com.mockobjects.dynamic.C;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceType;

import java.util.List;
import java.util.Arrays;
import java.util.Collection;

public class TestConfluenceProjectKeyLocator extends TestCase
{
    ConfluenceProjectManager confluenceProjectManager;
    Mock mockSpaceManager;

    public void setUp()
    {
        confluenceProjectManager = new ConfluenceProjectManager();
        mockSpaceManager = new Mock(SpaceManager.class);
        confluenceProjectManager.setSpaceManager((SpaceManager) mockSpaceManager.proxy());
    }

    public void testGetAllProjectKeys()
    {
        Space s1 = new Space();
        s1.setKey("s1");
        Space s2 = new Space();
        s2.setKey("s2");
        List<Space> spaces = Arrays.asList(s1, s2);
        mockSpaceManager.expectAndReturn("getSpacesByType", C.eq(SpaceType.GLOBAL), spaces);
        Collection<String> keys = confluenceProjectManager.getAllProjectKeys();
        assertEquals(2, keys.size());
        assertTrue(keys.contains("s1"));
        assertTrue(keys.contains("s2"));
        mockSpaceManager.verify();
    }
}
