package com.atlassian.sal.crowd.project;

import com.atlassian.sal.api.project.ProjectManager;

import java.util.Collection;
import java.util.Collections;

/**
 * Crowd project manager that returns no project keys, as this doesn't really apply to crowd
 *
 * @since 2.2.0
 */
public class CrowdProjectManager implements ProjectManager
{
    public Collection<String> getAllProjectKeys()
    {
        return Collections.emptySet();
    }
}
