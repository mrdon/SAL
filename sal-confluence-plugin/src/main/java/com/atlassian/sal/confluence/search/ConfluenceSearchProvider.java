package com.atlassian.sal.confluence.search;

import com.atlassian.bonnie.Searcher;
import com.atlassian.bonnie.search.summary.Summary;
import com.atlassian.confluence.core.Addressable;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.search.actions.SearchBean;
import com.atlassian.confluence.search.actions.SearchQueryBean;
import com.atlassian.confluence.search.actions.SearchResultWithExcerpt;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.GeneralUtil;
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
import com.atlassian.sal.api.search.query.DefaultQueryParser;
import com.atlassian.sal.api.search.query.QueryParser;
import com.atlassian.user.User;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    public SearchResults search(String username, String searchQuery)
    {
        QueryParser queryParser = new DefaultQueryParser(searchQuery);
        return search(queryParser.getSearchString(), queryParser.getMaxHits(), username);
    }

    private SearchResults search(String searchQuery, int maxHits, String username)
    {
        //set the user to be the user that was passed in
        final User oldUser = AuthenticatedUserThreadLocal.getUser();
        AuthenticatedUserThreadLocal.setUser(getUser(username));
        try
        {
            long startTime = System.currentTimeMillis();
            SearchQueryBean wiredSearchQueryBean = getWiredSearchQueryBean(searchQuery);
            List list = searchBean.search(wiredSearchQueryBean.buildQuery());

            return new SearchResults(transformSearchResults(searchQuery, maxHits, list), System.currentTimeMillis() - startTime);
        }
        catch (Exception e)
        {
            log.error("Error running confluence search for query '" + searchQuery + "'", e);
            List<Message> errors = new ArrayList<Message>();
            errors.add(new DefaultMessage(e.getMessage()));
            return new SearchResults(errors);
        }
        finally
        {
            //restore the user to who it was before running the search.
            AuthenticatedUserThreadLocal.setUser(oldUser);
        }
    }

    private List<SearchMatch> transformSearchResults(String searchQuery, int maxHits, List list)
    {
        List<SearchMatch> matches = new ArrayList<SearchMatch>();
        ApplicationProperties webProperties = getApplicationProperties();
        int count = 0;
        if (maxHits == -1)
        {
            maxHits = Integer.MAX_VALUE;
        }
        for (Iterator iterator = list.iterator(); iterator.hasNext() && count < maxHits; count++)
        {
            SearchResultWithExcerpt searchResultWithExcerpt = (SearchResultWithExcerpt) iterator.next();
            if (searchResultWithExcerpt.getResultObject() instanceof Addressable)
            {
                Addressable result = (Addressable) searchResultWithExcerpt.getResultObject();
                ResourceType resultType = new BasicResourceType(webProperties, result.getType());
                String excerpt = getExcerpt(searchQuery, searchResultWithExcerpt.getContentBodyString());
                matches.add(new BasicSearchMatch(webProperties.getBaseUrl() + result.getUrlPath(),
                        result.getRealTitle(), excerpt, resultType));
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

    SearchQueryBean getWiredSearchQueryBean(String searchQuery)
    {
        SearchQueryBean searchQueryBean = new SearchQueryBean();
        searchQueryBean.setSearcher(searcher);
        searchQueryBean.setUserAccessor(userAccessor);
        searchQueryBean.setSpaceManager(spaceManager);
        searchQueryBean.setLabelManager(labelManager);
        searchQueryBean.setSettingsManager(settingsManager);

        try
        {
            searchQueryBean.setQueryString(searchQuery);
        }
        catch (IOException e)
        {
            //should never happen.
            throw new RuntimeException(e);
        }

        return searchQueryBean;
    }

    String getExcerpt(String searchQuery, String contentBodyString)
    {
        if (StringUtils.isBlank(contentBodyString))
        {
            return "";
        }
        Summary summary = GeneralUtil.makeSummary(contentBodyString, searchQuery);
        StringBuffer excerpt = new StringBuffer();
        for (int i = 0; i < summary.getFragments().length; i++)
        {
            Summary.Fragment fragment = summary.getFragments()[i];
            if (fragment.isHighlight())
            {
                excerpt.append("<span class=\"highlight\">").
                        append(GeneralUtil.htmlEncode(fragment.toString())).append("</span>");
            }
            else
            {
                excerpt.append(GeneralUtil.htmlEncode(fragment.toString()));
            }
        }
        return excerpt.toString();
    }

    User getUser(String username)
    {
        return userAccessor.getUser(username);
    }
}
