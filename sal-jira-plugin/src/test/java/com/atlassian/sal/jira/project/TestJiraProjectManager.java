package com.atlassian.sal.jira.project;

import junit.framework.TestCase;
import com.mockobjects.dynamic.Mock;
import com.mockobjects.dynamic.C;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.project.ProjectFactory;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectImpl;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;

public class TestJiraProjectManager extends TestCase
{
    Mock mockProjectManager;
    Mock mockProjectFactory;
    JiraProjectManager jiraProjectManager;

    public void setUp()
    {
        mockProjectManager = new Mock(ProjectManager.class);
        mockProjectFactory = new Mock(ProjectFactory.class);
        jiraProjectManager = new JiraProjectManager((ProjectManager) mockProjectManager.proxy(),
            (ProjectFactory) mockProjectFactory.proxy());
    }

    public void testGetAllProjectKeys()
    {
        Collection projectGVs = new ArrayList();
        // What to do .....
        class MockProject extends ProjectImpl
        {
            private String key;

            public MockProject(String key)
            {
                super(null, null, null);
                this.key = key;
            }

            public String getKey()
            {
                return key;
            }

        }
        // .... that's simple enough
        Project p1 = new MockProject("p1");
        Project p2 = new MockProject("p2");
        Collection<Project> projects = Arrays.asList(p1, p2);

        mockProjectManager.expectAndReturn("getProjects", projectGVs);
        mockProjectFactory.expectAndReturn("getProjects", C.same(projectGVs), projects);
        Collection<String> keys = jiraProjectManager.getAllProjectKeys();
        assertEquals(2, keys.size());
        assertTrue(keys.contains("p1"));
        assertTrue(keys.contains("p2"));
        mockProjectFactory.verify();
        mockProjectManager.verify();
    }

}
