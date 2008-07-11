package com.atlassian.sal.core.search;

import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.search.ResourceType;
import com.atlassian.sal.api.search.SearchMatch;
import com.atlassian.sal.api.search.SearchResults;
import com.atlassian.sal.core.message.DefaultMessage;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class TestSearchResults extends TestCase
{

    public void testErrorsAndMatchesIsNotModifiablel()
    {
        SearchResults searchResults = new SearchResults(new ArrayList<Message>());
        List<Message> errors = searchResults.getErrors();
        try
        {
            errors.add(new DefaultMessage("stuff"));
            fail();
        }
        catch (UnsupportedOperationException e)
        {
            //yay!
        }
        List<SearchMatch> matches = searchResults.getMatches();
        try
        {
            matches.add(new SearchMatch()
            {

                public String getUrl()
                {
                    return null;
                }

                public String getTitle()
                {
                    return null;
                }

                public String getExcerpt()
                {
                    return null;
                }

                public ResourceType getResourceType()
                {
                    return null;
                }
            });
            fail();
        }
        catch (UnsupportedOperationException e)
        {
            //yay!
        }
    }

}
