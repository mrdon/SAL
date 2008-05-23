package com.atlassian.sal.fisheye.search;

import com.atlassian.sal.api.message.DefaultMessage;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.search.SearchProvider;
import com.atlassian.sal.api.search.SearchResults;
import com.atlassian.sal.api.search.parameter.SearchParameter;
import com.atlassian.sal.api.search.query.DefaultQueryParser;
import com.atlassian.sal.api.search.query.QueryParser;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class CenquaSearchProvider implements SearchProvider
{
    private static final String FISH_EYE = "FishEye";
    private static final String CRUCIBLE = "Crucible";

    public SearchResults search(String username, String searchQuery)
    {
        final QueryParser queryParser = new DefaultQueryParser(searchQuery);
        final List<Message> errors = validateQuery(queryParser);
        if (!errors.isEmpty())
        {
            return new SearchResults(errors);
        }

        final String application = queryParser.getParameter(SearchParameter.APPLICATION).getValue();
        if (FISH_EYE.equals(application))
        {
            return getFisheyeSearchProvider().search(username, searchQuery);
        }
        else if (CRUCIBLE.equals(application))
        {
            return getCrucibleSearchProvider().search(username, searchQuery);
        }
        //TODO: implement search of both Fisheye & Crucible with interleaved results.
        return null;
    }

    private List<Message> validateQuery(QueryParser queryParser)
    {
        final List<Message> errors = new ArrayList<Message>();
        final SearchParameter applicationParameter = queryParser.getParameter(SearchParameter.APPLICATION);
        if (applicationParameter == null)
        {
            errors.add(new DefaultMessage("studio.search.errors.search.param.missing", SearchParameter.APPLICATION));
        }
        else if (!FISH_EYE.equals(applicationParameter.getValue()) && !CRUCIBLE.equals(applicationParameter.getValue()))
        {
            errors.add(new DefaultMessage("studio.search.errors.search.param.invalid.value", applicationParameter.getValue(), applicationParameter.getName()));
        }

        return errors;
    }

    SearchProvider getFisheyeSearchProvider()
    {
        return new FisheyeSearchProvider();
    }

    SearchProvider getCrucibleSearchProvider()
    {
        return new CrucibleSearchProvider();
    }
}
