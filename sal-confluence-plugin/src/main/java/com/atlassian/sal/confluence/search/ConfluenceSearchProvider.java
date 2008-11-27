package com.atlassian.sal.confluence.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.atlassian.confluence.search.service.PredefinedSearchBuilder;
import com.atlassian.confluence.search.service.SearchQueryParameters;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.Search;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.search.ResourceType;
import com.atlassian.sal.api.search.SearchMatch;
import com.atlassian.sal.api.search.SearchProvider;
import com.atlassian.sal.api.search.SearchResults;
import com.atlassian.sal.api.search.parameter.SearchParameter;
import com.atlassian.sal.api.search.query.SearchQuery;
import com.atlassian.sal.api.search.query.SearchQueryParser;
import com.atlassian.sal.core.message.DefaultMessage;
import com.atlassian.sal.core.search.BasicResourceType;
import com.atlassian.sal.core.search.BasicSearchMatch;
import com.atlassian.user.User;

public class ConfluenceSearchProvider implements SearchProvider
{
    private static final Logger log = Logger.getLogger(ConfluenceSearchProvider.class);

    private final UserAccessor userAccessor;
    private final ApplicationProperties applicationProperties;
	private final SearchQueryParser searchQueryParser;

	private final PredefinedSearchBuilder predefinedSearchBuilder;

	private final SearchManager searchManager;

	public ConfluenceSearchProvider(final PredefinedSearchBuilder predefinedSearchBuilder, final SearchManager searchManager, final SearchQueryParser searchQueryParser,
									final UserAccessor userAccessor, final ApplicationProperties applicationProperties)
	{
		this.predefinedSearchBuilder = predefinedSearchBuilder;
		this.searchManager = searchManager;
		this.userAccessor = userAccessor;
		this.applicationProperties = applicationProperties;
		this.searchQueryParser = searchQueryParser;
	}


	public SearchResults search(final String username, final String stringQuery)
	{
		final long startTime = System.currentTimeMillis();

		//set the user to be the user that was passed in
		final User oldUser = AuthenticatedUserThreadLocal.getUser();
		AuthenticatedUserThreadLocal.setUser(userAccessor.getUser(username));

		try
		{
			final SearchQuery searchQuery = searchQueryParser.parse(stringQuery);
			final SearchQueryParameters searchQueryParams = new SearchQueryParameters(searchQuery.getSearchString());
			final String projectKey = searchQuery.getParameter(SearchParameter.PROJECT);
			if (StringUtils.isNotEmpty(projectKey))
			{
				searchQueryParams.setSpaceKey(projectKey);
			}
			final Search search = predefinedSearchBuilder.siteSearch(searchQueryParams, 0, searchQuery.getParameter(SearchParameter.MAXHITS, Integer.MAX_VALUE));
			final com.atlassian.confluence.search.v2.SearchResults result = searchManager.search(search);

			final List<SearchMatch> matches = new ArrayList<SearchMatch>();
			for (final SearchResult searchResult : result.getAll())
			{

				final String url = applicationProperties.getBaseUrl() + searchResult.getUrlPath();
				final String title = searchResult.getDisplayTitle();
				final String excerpt = searchResult.getContent();
				final ResourceType resourceType = new BasicResourceType(applicationProperties, searchResult.getType());
				matches.add(new BasicSearchMatch(url, title, excerpt, resourceType));
			}

			return new SearchResults(matches, result.getUnfilteredResultsCount(), System.currentTimeMillis() - startTime);
		} catch (final InvalidSearchException e)
		{
			log.error("Error running confluence search", e);
			final List<Message> errors = new ArrayList<Message>();
			errors.add(new DefaultMessage(e.getMessage()));
			return new SearchResults(errors);
        }
        finally
        {
            //restore the user to who it was before running the search.
            AuthenticatedUserThreadLocal.setUser(oldUser);
        }
	}

}
