package com.atlassian.sal.bamboo.pluginsettings;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.atlassian.bamboo.bandana.PlanAwareBandanaContext;
import com.atlassian.bamboo.build.Build;
import com.atlassian.bamboo.build.BuildManager;
import com.atlassian.bamboo.project.Project;
import com.atlassian.bamboo.project.ProjectManager;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.sal.api.pluginsettings.PluginSettings;

@SuppressWarnings({"JUnitTestMethodWithNoAssertions", "UnusedDeclaration"})
public class TestBambooPluginSettingsFactory
{
    private BambooPluginSettingsFactory bambooPluginSettingsFactory;
    @Mock
    private BandanaManager mockBandanaManager;
    @Mock
    private BuildManager mockBuildManager;
    @Mock
    private ProjectManager mockProjectManager;

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
        bambooPluginSettingsFactory = new BambooPluginSettingsFactory(mockBandanaManager, mockBuildManager, mockProjectManager);
    }

    @Test
    public void testCreateGlobalSettings()
    {
        final PluginSettings pluginSettings = bambooPluginSettingsFactory.createGlobalSettings();
        pluginSettings.get("key");
        verify(mockBandanaManager).getValue(PlanAwareBandanaContext.GLOBAL_CONTEXT, "key", false);
    }

    @Test
    public void testCreateSettingsForPlanKey()
    {
        final Build build = mock(Build.class);
        when(mockBuildManager.getBuildByKey("plan")).thenReturn(build);
        when(build.getId()).thenReturn(10L);
        final PluginSettings pluginSettings = bambooPluginSettingsFactory.createSettingsForKey("plan");
        pluginSettings.get("key");
        verify(mockBandanaManager).getValue(new PlanAwareBandanaContext(10L), "key", false);
    }

    @Test
    public void testCreateSettingsForProjectKey()
    {
        final Project project = mock(Project.class);
        when(mockProjectManager.getProjectByKey("project")).thenReturn(project);
        final PluginSettings pluginSettings = bambooPluginSettingsFactory.createSettingsForKey("project");
        pluginSettings.get("key");
        verify(mockBandanaManager).getValue(PlanAwareBandanaContext.GLOBAL_CONTEXT, "__project.key", false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateSettingsForKeyNotExist()
    {
        bambooPluginSettingsFactory.createSettingsForKey("nothing");
    }
}
