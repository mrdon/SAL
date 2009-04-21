package com.atlassian.sal.fisheye.appconfig;

import com.atlassian.crucible.spi.TxCallback;
import com.atlassian.fisheye.spi.TxTemplate;
import com.cenqua.crucible.actions.admin.project.ProjectData;
import com.cenqua.crucible.actions.admin.project.ProjectDataFactory;
import com.cenqua.crucible.model.Project;
import com.cenqua.crucible.model.managers.ProjectManager;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

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
        fisheyeAccessor = new DefaultFisheyeAccessor(mockProjectManager, mockProjectDataFactory, new TxTemplate()
        {
            @Override
            public <T> T execute(TxCallback<T> txCallback)
            {
                try
                {
                    return txCallback.doInTransaction(null);
                }
                catch (Exception e)
                {
                    // Do nothing
                }
                return null;
            }
        });
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
