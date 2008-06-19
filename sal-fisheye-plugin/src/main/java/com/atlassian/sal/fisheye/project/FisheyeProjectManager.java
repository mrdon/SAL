package com.atlassian.sal.fisheye.project;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

import com.cenqua.fisheye.AppConfig;
import com.cenqua.fisheye.rep.RepositoryHandle;
import com.atlassian.sal.api.project.ProjectManager;

/**
 * Fisheye implementation of the project key locator
 */
public class FisheyeProjectManager implements ProjectManager
{
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
        return results;
    }
}
