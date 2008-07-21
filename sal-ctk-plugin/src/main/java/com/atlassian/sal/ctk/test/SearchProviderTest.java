package com.atlassian.sal.ctk.test;

import com.atlassian.sal.ctk.CtkTest;
import com.atlassian.sal.ctk.CtkTestResults;
import com.atlassian.sal.api.component.ComponentLocator;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.project.ProjectManager;
import com.atlassian.sal.api.search.SearchProvider;
import com.atlassian.sal.api.search.SearchResults;
import com.atlassian.plugin.PluginManager;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class SearchProviderTest implements CtkTest
{
    private final SearchProvider searchProvider;

    public SearchProviderTest(SearchProvider searchProvider) {this.searchProvider = searchProvider;}


    public void execute(CtkTestResults results)
    {
        results.assertTrue("SearchProvider should be injectable", searchProvider != null);

        SearchResults sresults = searchProvider.search(null, "the");
        results.assertTrue("Should always return results", sresults != null);

        results.assertTrue("Search time should be greater than zero", sresults.getSearchTime() > 0);
    }
}