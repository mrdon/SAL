package com.atlassian.sal.ctk.test;

import org.springframework.stereotype.Component;

import com.atlassian.sal.api.search.SearchProvider;
import com.atlassian.sal.api.search.SearchResults;
import com.atlassian.sal.ctk.CtkTest;
import com.atlassian.sal.ctk.CtkTestResults;

@Component
public class SearchProviderTest implements CtkTest
{
    private final SearchProvider searchProvider;

    public SearchProviderTest(final SearchProvider searchProvider)
	{
		this.searchProvider = searchProvider;
	}

    public void execute(final CtkTestResults results)
    {
        results.assertTrue("SearchProvider should be injectable", searchProvider != null);

        final SearchResults sresults = searchProvider.search(null, "the");
        results.assertTrue("Should always return results", sresults != null);

        results.assertTrueOrWarn("Search time should be greater than zero", sresults.getSearchTime() > 0);
    }
}