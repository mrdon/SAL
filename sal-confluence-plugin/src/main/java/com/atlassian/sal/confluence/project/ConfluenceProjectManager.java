package com.atlassian.sal.confluence.project;

import com.atlassian.sal.api.project.ProjectManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceType;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * The confiluence implementation of the project manager, a project in the Confluence context is equivalent to a space
 */
public class ConfluenceProjectManager implements ProjectManager
{
    private SpaceManager spaceManager;

    /**
     * Get all project keys
     *
     * @return All the project keys
     */
    public Collection<String> getAllProjectKeys()
    {
        Collection<String> results = new HashSet<String>();
        List<Space> spaces = spaceManager.getSpacesByType(SpaceType.GLOBAL);
        for (Space space : spaces)
        {
            results.add(space.getKey());
        }
        return results;
    }

    public void setSpaceManager(SpaceManager spaceManager)
    {
        this.spaceManager = spaceManager;
    }
}
