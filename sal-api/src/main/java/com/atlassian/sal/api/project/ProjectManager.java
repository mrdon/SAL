package com.atlassian.sal.api.project;

import java.util.Collection;

/**
 * Interface to managing and getting information about "projects".  A project may represent different things depending
 * on the application, for example, in Confluence, it is a space, in Bamboo, a build plan, and in JIRA, it is a
 * project.
 *
 * @since 2.0
 */
public interface ProjectManager
{
    /**
     * Get all project keys
     *
     * @return All the project keys or an empty collection if it cannot be provided by the current application
     */
    Collection<String> getAllProjectKeys();
}
