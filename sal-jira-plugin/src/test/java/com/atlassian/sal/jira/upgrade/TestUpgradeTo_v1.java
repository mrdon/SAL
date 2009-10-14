package com.atlassian.sal.jira.upgrade;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import org.ofbiz.core.entity.GenericValue;
import org.ofbiz.core.entity.GenericEntityException;
import com.atlassian.jira.propertyset.JiraPropertySetFactory;
import com.opensymphony.module.propertyset.PropertySet;

import java.util.*;

public class TestUpgradeTo_v1
{
    private UpgradeTo_v1 upgradeTo_v1;
    @Mock
    private JiraPropertySetFactory jiraPropertySetFactory;
    private List<GenericValue> splitProperties;
    @Mock private Map<Long, GenericValue> entries;

    @Before
    public void setUp()
    {
        splitProperties = new ArrayList<GenericValue>();
        initMocks(this);
        upgradeTo_v1 = new UpgradeTo_v1(jiraPropertySetFactory) {
            protected GenericValue getPropertyEntry(long id) throws GenericEntityException
            {
                return entries.get(id);
            }

            protected List<GenericValue> getSplitProperties() throws GenericEntityException
            {
                return splitProperties;
            }
        };
    }

    @Test
    public void testUpgrade() throws Exception
    {
        GenericValue property = addValueToList(1L, "#-#-#3");
        GenericValue entry = addEntryToList(1L, "thename", 2L, "thekey");
        PropertySet propertySet = mock(PropertySet.class);
        when(jiraPropertySetFactory.buildNoncachingPropertySet("thename", 2L)).thenReturn(propertySet);
        when(propertySet.getString("thekey#-#-#0")).thenReturn("part1");
        when(propertySet.getString("thekey#-#-#1")).thenReturn("part2");
        when(propertySet.getString("thekey#-#-#2")).thenReturn("part3");
        upgradeTo_v1.doUpgrade();
        // Verify the correct new value was put in
        verify(propertySet).setText("thekey", "part1part2part3");
        // Verify the old keys were deleted
        verify(propertySet).remove("thekey");
        verify(propertySet).remove("thekey#-#-#0");
        verify(propertySet).remove("thekey#-#-#1");
        verify(propertySet).remove("thekey#-#-#2");
    }

    @Test
    public void testUpgradeNoEntry() throws Exception
    {
        GenericValue property = addValueToList(1L, "#-#-#3");
        upgradeTo_v1.doUpgrade();
    }

    @Test
    public void testUpgradeMissingValue() throws Exception
    {
        GenericValue property = addValueToList(1L, "#-#-#3");
        GenericValue entry = addEntryToList(1L, "thename", 2L, "thekey");
        PropertySet propertySet = mock(PropertySet.class);
        when(jiraPropertySetFactory.buildNoncachingPropertySet("thename", 2L)).thenReturn(propertySet);
        when(propertySet.getString("thekey#-#-#0")).thenReturn("part1");
        when(propertySet.getString("thekey#-#-#2")).thenReturn("part3");
        upgradeTo_v1.doUpgrade();
        // Verify the correct new value was put in
        verify(propertySet).setText("thekey", "part1part3");
        // Verify the old keys were deleted
        verify(propertySet).remove("thekey");
        verify(propertySet).remove("thekey#-#-#0");
        verify(propertySet).remove("thekey#-#-#2");
    }


    private GenericValue addEntryToList(long id, String entityName, long entityId, String key)
    {
        GenericValue gv = mock(GenericValue.class);
        when(gv.getString("entityName")).thenReturn(entityName);
        when(gv.getLong("entityId")).thenReturn(entityId);
        when(gv.getString("propertyKey")).thenReturn(key);
        when(entries.get(id)).thenReturn(gv);
        return gv;
    }

    private GenericValue addValueToList(long id, String value)
    {
        GenericValue gv = mock(GenericValue.class);
        when(gv.getLong("id")).thenReturn(id);
        when(gv.getString("value")).thenReturn(value);
        splitProperties.add(gv);
        return gv;
    }
}
