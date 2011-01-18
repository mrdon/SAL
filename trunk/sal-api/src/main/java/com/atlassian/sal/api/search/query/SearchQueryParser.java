package com.atlassian.sal.api.search.query;


/**
 * Parses a query string created by a {@link SearchQuery} to allow easy access to the various aspects of the query
 *
 * @since 2.0
 */
public interface SearchQueryParser
{

    /**
     * Query string submitted should already be url-encoded.
     *
     * @param query The search query
     * @return The parsed query
     */
    SearchQuery parse(String query);
}
