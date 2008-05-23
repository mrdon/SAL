package com.atlassian.sal.api.search;

import com.atlassian.sal.api.message.Message;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Provides searchresults for a query.  If there were any errors, check the list of errors.  The searchresult will also
 * contain the total searchtime (in milliseconds).
 */
public class SearchResults
{
    private final List<Message> errors = new ArrayList<Message>();
    private final List<SearchMatch> matches = new ArrayList<SearchMatch>();
    private long searchTime = 0;
    private int totalResults = 0;

    public SearchResults(List<Message> errors)
    {
        this.errors.addAll(errors);
    }

    public SearchResults(List<SearchMatch> matches, int totalResults, long searchTime)
    {
        this.totalResults = totalResults;
        this.matches.addAll(matches);
        this.searchTime = searchTime;
    }

    public List<Message> getErrors()
    {
        return Collections.unmodifiableList(errors);
    }

    public List<SearchMatch> getMatches()
    {
        return Collections.unmodifiableList(matches);
    }

    public long getSearchTime()
    {
        return searchTime;
    }

    public int getTotalResults()
    {
        return totalResults;
    }
}
