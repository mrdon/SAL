package com.atlassian.sal.confluence.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.atlassian.bonnie.Searcher;
import com.atlassian.confluence.core.Addressable;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.search.actions.SearchBean;
import com.atlassian.confluence.search.actions.SearchQueryBean;
import com.atlassian.confluence.search.actions.SearchResultWithExcerpt;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.component.ComponentLocator;
import com.atlassian.sal.api.message.DefaultMessage;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.search.BasicResourceType;
import com.atlassian.sal.api.search.BasicSearchMatch;
import com.atlassian.sal.api.search.ResourceType;
import com.atlassian.sal.api.search.SearchMatch;
import com.atlassian.sal.api.search.SearchProvider;
import com.atlassian.sal.api.search.SearchResults;
import com.atlassian.sal.api.search.parameter.SearchParameter;
import com.atlassian.sal.api.search.query.SearchQuery;
import com.atlassian.sal.api.search.query.SearchQueryParser;
import com.atlassian.user.User;

/**
 */
public class ConfluenceSearchProvider implements SearchProvider
{
    private static final Logger log = Logger.getLogger(ConfluenceSearchProvider.class);

    private SearchBean searchBean;
    private Searcher searcher;
    private UserAccessor userAccessor;
    private SpaceManager spaceManager;
    private LabelManager labelManager;
    private SettingsManager settingsManager;

    public SearchResults search(String username, String stringQuery)
    {
        SearchQueryParser queryParser = ComponentLocator.getComponent(SearchQueryParser.class);
        final SearchQuery searchQuery = queryParser.parse(stringQuery);
        SearchResults result;
        //set the user to be the user that was passed in
        final User oldUser = AuthenticatedUserThreadLocal.getUser();
        AuthenticatedUserThreadLocal.setUser(getUser(username));
        try
        {
            long startTime = System.currentTimeMillis();
			SearchQueryBean wiredSearchQueryBean = getWiredSearchQueryBean(searchQuery);
			List list = searchBean.search(wiredSearchQueryBean.buildQuery());
			int maxHits = searchQuery.getParameter(SearchParameter.MAXHITS, Integer.MAX_VALUE);
            result = new SearchResults(transformSearchResults(maxHits, list), list.size(), System.currentTimeMillis() - startTime);
        }
        catch (Exception e)
        {
            log.error("Error running confluence search", e);
            List<Message> errors = new ArrayList<Message>();
            errors.add(new DefaultMessage(e.getMessage()));
            result = new SearchResults(errors);
        }
        finally
        {
            //restore the user to who it was before running the search.
            AuthenticatedUserThreadLocal.setUser(oldUser);
        }
        return result;
    }

    private List<SearchMatch> transformSearchResults(int maxHits, List list)
    {
        List<SearchMatch> matches = new ArrayList<SearchMatch>();
        ApplicationProperties webProperties = getApplicationProperties();
        int count = 0;
        for (Iterator iterator = list.iterator(); iterator.hasNext() && count < maxHits; count++)
        {
            SearchResultWithExcerpt searchResultWithExcerpt = (SearchResultWithExcerpt) iterator.next();
            if (searchResultWithExcerpt.getResultObject() instanceof Addressable)
            {
                Addressable result = (Addressable) searchResultWithExcerpt.getResultObject();
                ResourceType resultType = new BasicResourceType(webProperties, result.getType());
                matches.add(new BasicSearchMatch(webProperties.getBaseUrl() + result.getUrlPath(),
                        result.getRealTitle(), searchResultWithExcerpt.getContentBodyString(), resultType));
            }
        }
        return matches;
    }

    public void setSearcher(Searcher searcher)
    {
        this.searcher = searcher;
    }

    public void setUserAccessor(UserAccessor userAccessor)
    {
        this.userAccessor = userAccessor;
    }

    public void setSpaceManager(SpaceManager spaceManager)
    {
        this.spaceManager = spaceManager;
    }

    public void setLabelManager(LabelManager labelManager)
    {
        this.labelManager = labelManager;
    }

    public void setSettingsManager(SettingsManager settingsManager)
    {
        this.settingsManager = settingsManager;
    }

    public void setSearchBean(SearchBean searchBean)
    {
        this.searchBean = searchBean;
    }

    ApplicationProperties getApplicationProperties()
    {
        return ComponentLocator.getComponent(ApplicationProperties.class);
    }

    SearchQueryBean getWiredSearchQueryBean(SearchQuery query)
    {
        SearchQueryBean searchQueryBean = new SearchQueryBean();
        searchQueryBean.setSearcher(searcher);
        searchQueryBean.setUserAccessor(userAccessor);
        searchQueryBean.setSpaceManager(spaceManager);
        searchQueryBean.setLabelManager(labelManager);
        searchQueryBean.setSettingsManager(settingsManager);
        final String projectKey = query.getParameter(SearchParameter.PROJECT);
        if (StringUtils.isNotEmpty(projectKey))
        {
            searchQueryBean.setSpaceKey(projectKey);
        }

        try
        {
            searchQueryBean.setQueryString(query.getSearchString());
        }
        catch (IOException e)
        {
            //should never happen.
            throw new RuntimeException(e);
        }

        return searchQueryBean;
    }

    User getUser(String username)
    {
        return userAccessor.getUser(username);
    }
}
