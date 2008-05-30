package com.atlassian.sal.api.search.query;


/**
 * Factory for creating SearchQueries
 */
public class DefaultSearchQueryParser implements SearchQueryParser
{
	public SearchQuery parse(String query)
	{
		return new DefaultSearchQuery(query);
	}
}
