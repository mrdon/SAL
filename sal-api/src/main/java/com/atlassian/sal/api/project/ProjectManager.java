package com.atlassian.sal.api.project;

import java.util.Collection;

/**
 * Interface to managing and getting information about "projects".  A project may represent different things depending
 * on the application, for example, in Confluence, it is a space, in Bamboo, a build plan, and in JIRA, it is a
 * project.
 */
public interface ProjectManager
{
    /**
     * Get all project keys
     *
     * @return All the project keys
     */
    Collection<String> getAllProjectKeys();
}
