package com.atlassian.sal.api.search;

import com.atlassian.sal.api.message.Message;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Provides searchresults for a query.  If there were any errors, check the list of errors.  The searchresult will also
 * contain the total searchtime (in milliseconds).
 *
 * @since 2.0
 */
public class SearchResults
{
    private final List<Message> errors = new ArrayList<Message>();
    private final List<SearchMatch> matches = new ArrayList<SearchMatch>();
    private final long searchTime;
    private final int totalResults;

    /**
     * Constructs search results that contained errors
     *
     * @param errors The error list
     */
    public SearchResults(List<Message> errors)
    {
        this.errors
                .addAll(errors);
        searchTime = 0L;
        totalResults = 0;
    }

    /**
     * Constructs search results with successful matches
     *
     * @param matches      The list of matches
     * @param totalResults The total number of available results
     * @param searchTime   The time the search took in milliseconds
     */
    public SearchResults(List<SearchMatch> matches, int totalResults, long searchTime)
    {
        this.totalResults = totalResults;
        this.matches
                .addAll(matches);
        this.searchTime = searchTime;
    }

    /**
     * @return search errors
     */
    public List<Message> getErrors()
    {
        return Collections.unmodifiableList(errors);
    }

    /**
     * @return the matches
     */
    public List<SearchMatch> getMatches()
    {
        return Collections.unmodifiableList(matches);
    }

    /**
     * @return the time the search took in milliseconds
     */
    public long getSearchTime()
    {
        return searchTime;
    }

    /**
     * @return the total results available
     */
    public int getTotalResults()
    {
        return totalResults;
    }
}
