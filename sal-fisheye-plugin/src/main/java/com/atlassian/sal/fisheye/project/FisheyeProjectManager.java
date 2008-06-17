package com.atlassian.sal.fisheye.project;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

import com.atlassian.sal.api.project.ProjectManager;
import com.cenqua.fisheye.AppConfig;
import com.cenqua.fisheye.rep.RepositoryHandle;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Fisheye implementation of the project key locator
 */
public class FisheyeProjectManager implements com.atlassian.sal.api.project.ProjectManager
{
    @Autowired
    private com.cenqua.crucible.model.managers.ProjectManager projectManager;

    /**
     * Get all project keys
     *
     * @return All the project keys
     */
    public Collection<String> getAllProjectKeys()
    {
        // Get FishEye projects
        Collection<String> results = new ArrayList<String>();
        List<RepositoryHandle> handles = AppConfig.getsConfig().getRepositoryManager().getHandles();
        for (RepositoryHandle handle : handles)
        {
            results.add(handle.getName());
        }
        // Get crucible projects
        results.addAll(projectManager.getAllProjectKeys());
        return results;
    }

    public void setProjectManager(com.cenqua.crucible.model.managers.ProjectManager projectManager)
    {
        this.projectManager = projectManager;
    }
}
