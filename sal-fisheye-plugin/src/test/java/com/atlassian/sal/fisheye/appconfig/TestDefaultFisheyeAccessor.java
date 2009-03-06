package com.atlassian.sal.fisheye.appconfig;

import com.cenqua.crucible.model.managers.ProjectManager;
import com.cenqua.crucible.model.Project;
import com.cenqua.crucible.actions.admin.project.ProjectDataFactory;
import com.cenqua.crucible.actions.admin.project.ProjectData;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;
import static junit.framework.Assert.*;

public class TestDefaultFisheyeAccessor
{
    private ProjectManager mockProjectManager;
    private ProjectDataFactory mockProjectDataFactory;
    private DefaultFisheyeAccessor fisheyeAccessor;

    @Before
    public void setUp()
    {
        mockProjectManager = mock(ProjectManager.class);
        mockProjectDataFactory = mock(ProjectDataFactory.class);
        fisheyeAccessor = new DefaultFisheyeAccessor(mockProjectManager, mockProjectDataFactory);
    }

    @Test
    public void getProjectByKey()
    {
        Project project = mock(Project.class);
        when(project.getId()).thenReturn(10);
        when(mockProjectManager.getProjectByKey("KEY")).thenReturn(project);
        ProjectData result = fisheyeAccessor.getProjectByKey("KEY");
        assertEquals(10, result.getId());
    }

    @Test
    public void getProjectByKeyNull()
    {
        ProjectData result = fisheyeAccessor.getProjectByKey("KEY");
        assertNull(result);
    }

    @Test
    public void updateProject()
    {
        ProjectData pd = new ProjectData();
        pd.setId(10);
        fisheyeAccessor.updateProject(pd);
        verify(mockProjectDataFactory).updateProject(mockProjectManager, pd);
    }

}
