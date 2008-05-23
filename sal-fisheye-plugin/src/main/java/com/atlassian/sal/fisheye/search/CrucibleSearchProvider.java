package com.atlassian.sal.fisheye.search;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.component.ComponentLocator;
import com.atlassian.sal.api.search.BasicResourceType;
import com.atlassian.sal.api.search.BasicSearchMatch;
import com.atlassian.sal.api.search.SearchMatch;
import com.atlassian.sal.api.search.SearchProvider;
import com.atlassian.sal.api.search.SearchResults;
import com.atlassian.sal.api.search.query.DefaultQueryParser;
import com.atlassian.sal.api.search.query.QueryParser;
import com.cenqua.crucible.model.Principal;
import com.cenqua.crucible.model.Review;
import com.cenqua.crucible.model.managers.ReviewManager;
import com.cenqua.crucible.model.managers.UserActionManager;
import com.cenqua.crucible.tags.ReviewUtil;
import com.cenqua.crucible.util.ReviewSearchTerms;
import com.cenqua.fisheye.AppConfig;
import com.cenqua.fisheye.LicensePolicyException;
import com.cenqua.fisheye.rep.DbException;
import com.cenqua.fisheye.user.UserManager;
import com.cenqua.fisheye.util.NaturalComparator;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Implements Crucible search.  Only reviews that the user has permission to view will be returned with the search
 * results.
 */
public class CrucibleSearchProvider implements SearchProvider
{
    private static final Logger log = Logger.getLogger(CrucibleSearchProvider.class);

    public SearchResults search(String username, String searchQuery)
    {
        QueryParser queryParser = new DefaultQueryParser(searchQuery);
        int maxHits = queryParser.getMaxHits();
        if (maxHits == -1)
        {
            maxHits = Integer.MAX_VALUE;
        }
        return doCrucibleSearch(queryParser.getSearchString(), maxHits, username);
    }

    private SearchResults doCrucibleSearch(String searchQuery, int maxHits, String username)
    {
        long startTime = System.currentTimeMillis();

        final List<Integer> resultIds = getReviewIds(searchQuery);
        final List<SearchMatch> matches = transformCrucibleResults(resultIds, maxHits, username);

        return new SearchResults(matches, resultIds.size(), System.currentTimeMillis() - startTime);
    }

    private List<SearchMatch> transformCrucibleResults(List<Integer> resultIds, int maxHits, String username)
    {
        final List<SearchMatch> matches = new ArrayList<SearchMatch>();
        final ApplicationProperties applicationProperties = ComponentLocator.getComponent(ApplicationProperties.class);
        int count = 0;
        for (Integer resultId : resultIds)
        {
            if (count > maxHits)
            {
                break;
            }
            final Review review = ReviewManager.getReviewById(resultId);
            if (hasPermissionToView(username, review))
            {
                final BasicSearchMatch searchMatch =
                        new BasicSearchMatch(review.getLink(), review.getName(), review.getDescription(),
                                new BasicResourceType(applicationProperties, "review"));
                matches.add(searchMatch);
                count++;
            }
        }
        return matches;
    }

    private List<Integer> getReviewIds(String searchQuery)
    {
        final ReviewSearchTerms terms = new ReviewSearchTerms(searchQuery);
        final List<Integer> resultIds = new LinkedList<Integer>();
        boolean first = true;
        for (Iterator iterator = terms.getAllTerms().iterator(); iterator.hasNext();)
        {
            String term = (String) iterator.next();
            final Set resultSet = ReviewManager.searchReviewForTerm(term, "review.id", "review.id");
            if (first)
            {
                resultIds.addAll(resultSet);
                first = false;
            }
            else
            {
                resultIds.retainAll(resultSet);
            }
        }
        Collections.sort(resultIds, NaturalComparator.REVERSE_INSTANCE);
        return resultIds;
    }

    private boolean hasPermissionToView(String username, Review review)
    {
        Principal userLogin;
        //if no username was supplied use an anonymous user.
        if (username == null)
        {
            userLogin = Principal.Anonymous.ANON;
        }
        else
        {
            try
            {
                final UserManager um = AppConfig.getsConfig().getUserManager();
                userLogin = um.createTrustedUserLogin(username);
            }
            catch (DbException e)
            {
                log.warn("Invalid user '" + username + "' tried to view review. Access denied.");
                return false;
            }
            catch (LicensePolicyException e)
            {
                log.warn("License exception when user '" + username + "' tried to view review. Access denied.");
                return false;
            }
        }
        return ReviewUtil.principalCanDoReviewAction(userLogin, UserActionManager.ACTION_VIEW, review);
    }
}
