package com.atlassian.sal.fisheye.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.search.SearchMatch;
import com.atlassian.sal.api.search.SearchProvider;
import com.atlassian.sal.api.search.SearchResults;
import com.atlassian.sal.api.search.parameter.SearchParameter;
import com.atlassian.sal.api.search.query.SearchQuery;
import com.atlassian.sal.api.search.query.SearchQueryParser;
import com.atlassian.sal.core.search.BasicSearchMatch;
import com.atlassian.sal.core.search.BasicResourceType;
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

/**
 * Implements Crucible search.  Only reviews that the user has permission to view will be returned with the search
 * results.
 */
public class CrucibleSearchProvider implements SearchProvider
{
    private static final Logger log = Logger.getLogger(CrucibleSearchProvider.class);
    private final SearchQueryParser queryParser;
    private final ApplicationProperties applicationProperties;

    public CrucibleSearchProvider(SearchQueryParser queryParser, ApplicationProperties applicationProperties)
    {
        this.queryParser = queryParser;
        this.applicationProperties = applicationProperties;
    }

    public SearchResults search(String username, String searchString)
    {
        final SearchQuery searchQuery = queryParser.parse(searchString);

		int maxHits = searchQuery.getParameter(SearchParameter.MAXHITS, Integer.MAX_VALUE);
        final String projectKey = searchQuery.getParameter(SearchParameter.PROJECT);

        long startTime = System.currentTimeMillis();

        final List<Integer> resultIds = getReviewIds(searchQuery.getSearchString(), projectKey, username);
        final List<SearchMatch> matches = transformCrucibleResults(resultIds, maxHits, username);

        return new SearchResults(matches, resultIds.size(), System.currentTimeMillis() - startTime);
    }

    private List<SearchMatch> transformCrucibleResults(List<Integer> resultIds, int maxHits, String username)
    {
        final List<SearchMatch> matches = new ArrayList<SearchMatch>();
        for (Integer resultId : resultIds)
        {
            if (matches.size() >= maxHits)
            {
                break;
            }
	        final Review review = ReviewManager.getReviewById(resultId);
            final BasicSearchMatch searchMatch =
                    new BasicSearchMatch(review.getLink(), review.getName(), review.getDescription(),
                            new BasicResourceType(applicationProperties, "review"));
            matches.add(searchMatch);
        }
        return matches;
    }

    private boolean isInProject(Review review, String projectKey)
    {
        //no project key specified.  return true
        if (projectKey == null || projectKey.length() == 0)
        {
            return true;
        }

        return projectKey.equals(review.getProject().getKey());
    }

    private List<Integer> getReviewIds(String searchQuery, String projectKey, String username)
    {
        final ReviewSearchTerms terms = new ReviewSearchTerms(searchQuery);
        final List<Integer> resultIds = new LinkedList<Integer>();
        boolean first = true;
        for(String term : terms.getAllTerms())
        {
            final Set resultSet = ReviewManager.searchReviewForTerm(term, "review.id", "review.id");
            if(first)
            {
                resultIds.addAll(resultSet);
                first = false;
            }
            else
            {
                resultIds.retainAll(resultSet);
            }
        }

        //some crude filtering here to remove all id's not in the right project. Would be nicer if
        //Crucible would support this for us...
        for (Iterator<Integer> resultIterator = resultIds.iterator(); resultIterator.hasNext();)
        {
            Integer resultId = resultIterator.next();
            Review review = ReviewManager.getReviewById(resultId);
            if (!hasPermissionToView(username, review) || !isInProject(review, projectKey))
            {
                resultIterator.remove();
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
