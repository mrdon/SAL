package com.atlassian.sal.fisheye.search;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.search.SearchProvider;
import com.atlassian.sal.api.search.SearchResults;
import com.atlassian.sal.api.search.parameter.SearchParameter;
import com.atlassian.sal.api.search.query.SearchQuery;
import com.atlassian.sal.api.search.query.SearchQueryParser;
import com.atlassian.sal.core.message.DefaultMessage;

/**
 */
public class CenquaSearchProvider implements SearchProvider
{
    private static final String FISH_EYE = "FishEye";
    private static final String CRUCIBLE = "Crucible";
    private final SearchQueryParser searchQueryParser;
    private final ApplicationProperties applicationProperties;

    public CenquaSearchProvider(final SearchQueryParser queryParser, final ApplicationProperties applicationProperties)
    {
        this.searchQueryParser = queryParser;
        this.applicationProperties = applicationProperties;
    }

    public SearchResults search(final String username, final String searchString)
    {
        final SearchQuery searchQuery = searchQueryParser.parse(searchString);
        final List<Message> errors = validateQuery(searchQuery);
        if (!errors.isEmpty())
        {
            return new SearchResults(errors);
        }

        final String application = searchQuery.getParameter(SearchParameter.APPLICATION);
        if (FISH_EYE.equals(application))
        {
            return getFisheyeSearchProvider().search(username, searchString);
        }
        else if (CRUCIBLE.equals(application))
        {
            return getCrucibleSearchProvider().search(username, searchString);
        }
        //TODO: implement search of both Fisheye & Crucible with interleaved results.
        return null;
    }

    private List<Message> validateQuery(final SearchQuery searchQuery)
    {
        final List<Message> errors = new ArrayList<Message>();
        final String applicationParameter = searchQuery.getParameter(SearchParameter.APPLICATION);
        if (applicationParameter == null)
        {
            errors.add(new DefaultMessage("studio.search.errors.search.param.missing", SearchParameter.APPLICATION));
        }
        else if (!FISH_EYE.equals(applicationParameter) && !CRUCIBLE.equals(applicationParameter))
        {
            errors.add(new DefaultMessage("studio.search.errors.search.param.invalid.value", applicationParameter, SearchParameter.APPLICATION));
        }

        return errors;
    }

    SearchProvider getFisheyeSearchProvider()
    {
        return new FisheyeSearchProvider(searchQueryParser, applicationProperties);
    }

    SearchProvider getCrucibleSearchProvider()
    {
        return new CrucibleSearchProvider(searchQueryParser, applicationProperties);
    }
}
