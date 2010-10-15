package com.atlassian.sal.bamboo.search;

import org.apache.log4j.Logger;
import com.atlassian.sal.api.search.SearchProvider;
import com.atlassian.sal.api.search.SearchResults;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.core.message.DefaultMessage;

import java.util.List;
import java.util.ArrayList;

/**
 * Search is not currently available in Bamboo so this is stubbed out.
 */
public class BambooSearchProvider implements SearchProvider
{
    private static final Logger log = Logger.getLogger(BambooSearchProvider.class);
    // ------------------------------------------------------------------------------------------------------- Constants
    // ------------------------------------------------------------------------------------------------- Type Properties
    // ---------------------------------------------------------------------------------------------------- Dependencies
    // ---------------------------------------------------------------------------------------------------- Constructors
    // ----------------------------------------------------------------------------------------------- Interface Methods
    // -------------------------------------------------------------------------------------------------- Action Methods
    // -------------------------------------------------------------------------------------------------- Public Methods
    public SearchResults search(String username, String searchQuery)
    {
        List<Message> errors = new ArrayList<Message>();
        errors.add(new DefaultMessage("Searching in Bamboo is not yet supported"));
        return new SearchResults(errors);
    }
    // ------------------------------------------------------------------------------------------------- Helper Methods
    // -------------------------------------------------------------------------------------- Basic Accessors / Mutators
}
