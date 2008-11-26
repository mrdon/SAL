package com.atlassian.sal.ctk.test;

import java.util.Collection;

import org.springframework.stereotype.Component;

import com.atlassian.sal.api.project.ProjectManager;
import com.atlassian.sal.ctk.CtkTest;
import com.atlassian.sal.ctk.CtkTestResults;

@Component
public class ProjectManagerTest implements CtkTest
{
    private final ProjectManager projectManager;

    public ProjectManagerTest(final ProjectManager projectManager)
	{
		this.projectManager = projectManager;
	}

    public void execute(final CtkTestResults results)
    {
        results.assertTrue("ProjectManager should be injectable", projectManager != null);

        final Collection<String> keys = projectManager.getAllProjectKeys();
        results.assertTrue("Project manager should return keys: "+keys, keys != null);
    }
}