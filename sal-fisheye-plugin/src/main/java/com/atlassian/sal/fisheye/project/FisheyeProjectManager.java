package com.atlassian.sal.fisheye.project;

import java.util.Collection;

import com.atlassian.sal.api.project.ProjectManager;
import com.atlassian.sal.fisheye.appconfig.FisheyeAccessor;

/**
 * Fisheye implementation of the project key locator
 */
public class FisheyeProjectManager implements ProjectManager
{

    private final FisheyeAccessor fisheyeAccessor;

    public FisheyeProjectManager(final FisheyeAccessor fisheyeAccessor)
    {
        this.fisheyeAccessor = fisheyeAccessor;
    }

    /**
     * Get all project keys
     *
     * @return All the project keys
     */
    public Collection<String> getAllProjectKeys()
    {
        return fisheyeAccessor.getRepositoryNames();
    }
}
