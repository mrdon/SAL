package com.atlassian.sal.jira.pluginsettings;

import org.mockito.Mock;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import com.atlassian.jira.propertyset.JiraPropertySetFactory;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.project.Project;
import com.opensymphony.module.propertyset.PropertySet;

public class TestLazyProjectMigratingPropertySet
{

    private PropertySet lazyProjectMigratingPropertySet;
    @Mock
    private JiraPropertySetFactory jiraPropertySetFactory;
    @Mock
    private ProjectManager projectManager;
    @Mock
    private PropertySet targetPropertySet;

    private static final String KEY = "KEY";

    @Before
    public void setUp()
    {
        initMocks(this);
        lazyProjectMigratingPropertySet = LazyProjectMigratingPropertySet.create(projectManager, jiraPropertySetFactory,
            targetPropertySet, KEY);
    }

    @Test
    public void testNormalMethod()
    {
        lazyProjectMigratingPropertySet.getString(KEY);
        verify(targetPropertySet).getString(KEY);
        verifyZeroInteractions(jiraPropertySetFactory);
    }

    @Test
    public void testNoMigrationNeeded()
    {
        when(targetPropertySet.exists(KEY)).thenReturn(true);
        assertTrue(lazyProjectMigratingPropertySet.exists(KEY));
        verify(targetPropertySet).exists(KEY);
        verifyNoMoreInteractions(targetPropertySet);
        verifyZeroInteractions(jiraPropertySetFactory);
    }

    @Test
    public void testMissNotProjectKey()
    {
        assertFalse(lazyProjectMigratingPropertySet.exists(KEY));
        verify(targetPropertySet).exists(KEY);
        verify(projectManager).getProjectObjByKey(KEY);
        // Try again
        assertFalse(lazyProjectMigratingPropertySet.exists(KEY));
        // Should be cached
        verifyNoMoreInteractions(projectManager);
    }

    @Test
    public void testMissOnProjectNoFallback()
    {
        PropertySet propertySet = setupExistingProject();
        assertFalse(lazyProjectMigratingPropertySet.exists(KEY));
        verify(propertySet).exists(KEY);
        verifyNoMoreInteractions(propertySet);
    }

    @Test
    public void testMissWithMigrateString()
    {
        PropertySet propertySet = setupExistingProject();
        when(propertySet.exists(KEY)).thenReturn(true);
        when(propertySet.getType(KEY)).thenReturn(PropertySet.STRING);
        when(propertySet.getString(KEY)).thenReturn("value");
        assertTrue(lazyProjectMigratingPropertySet.exists(KEY));
        verify(targetPropertySet).setString(KEY, "value");
        verify(propertySet).remove(KEY);
    }

    @Test
    public void testMissWithMigrateText()
    {
        PropertySet propertySet = setupExistingProject();
        when(propertySet.exists(KEY)).thenReturn(true);
        when(propertySet.getType(KEY)).thenReturn(PropertySet.TEXT);
        when(propertySet.getText(KEY)).thenReturn("value");
        assertTrue(lazyProjectMigratingPropertySet.exists(KEY));
        verify(targetPropertySet).setText(KEY, "value");
        verify(propertySet).remove(KEY);
    }

    @Test
    public void testMissWithUnmigratableType()
    {
        PropertySet propertySet = setupExistingProject();
        when(propertySet.exists(KEY)).thenReturn(true);
        when(propertySet.getType(KEY)).thenReturn(PropertySet.INT);
        assertFalse(lazyProjectMigratingPropertySet.exists(KEY));
        verify(propertySet).exists(KEY);
        verify(propertySet).getType(KEY);
        verify(targetPropertySet).exists(KEY);
        verifyNoMoreInteractions(targetPropertySet, propertySet);
    }

    private PropertySet setupExistingProject()
    {
        Project project = mock(Project.class);
        when(project.getId()).thenReturn(1L);
        when(projectManager.getProjectObjByKey(KEY)).thenReturn(project);

        PropertySet propertySet = mock(PropertySet.class);
        when(jiraPropertySetFactory.buildCachingPropertySet("Project", 1L, true)).thenReturn(propertySet);
        return propertySet;
    }
}
