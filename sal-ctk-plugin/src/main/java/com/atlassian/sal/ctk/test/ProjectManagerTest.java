package com.atlassian.sal.ctk.test;

import com.atlassian.sal.ctk.CtkTest;
import com.atlassian.sal.ctk.CtkTestResults;
import com.atlassian.sal.api.component.ComponentLocator;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.project.ProjectManager;
import com.atlassian.plugin.PluginManager;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class ProjectManagerTest implements CtkTest
{
    private final ProjectManager projectManager;

    public ProjectManagerTest(ProjectManager projectManager) {this.projectManager = projectManager;}


    public void execute(CtkTestResults results)
    {
        results.assertTrue("ProjectManager should be injectable", projectManager != null);

        Collection<String> keys = projectManager.getAllProjectKeys();
        results.assertTrue("Project manager should return keys: "+keys, keys != null);
    }
}