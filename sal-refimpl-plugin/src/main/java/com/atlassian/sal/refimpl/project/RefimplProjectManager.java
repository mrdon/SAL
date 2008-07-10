package com.atlassian.sal.refimpl.project;

import java.util.Arrays;
import java.util.Collection;

/**
 * Returns "FOO" and "BAR"
 */
public class RefimplProjectManager implements com.atlassian.sal.api.project.ProjectManager
{

    /**
     * Get all project keys
     *
     * @return All the project keys
     */
    public Collection<String> getAllProjectKeys()
    {
        return Arrays.asList("FOO", "BAR");
    }

}
