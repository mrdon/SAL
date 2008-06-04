package com.atlassian.sal.api.search.query;


/**
 * Parses a query string created by a {@link SearchQuery} to allow easy access to the various aspects of the query
 */
public interface SearchQueryParser
{

	/**
     * Query string submitted should already be url-encoded.
	 * @param query
	 * @return
	 */
	SearchQuery parse(String query);
}
