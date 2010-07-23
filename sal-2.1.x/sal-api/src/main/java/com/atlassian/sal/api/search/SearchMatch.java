package com.atlassian.sal.api.search;

/**
 * A single match for a query (i.e. and issue, a wiki page, a commit).  The Match contains a URL, Title and possibly
 * an excerpt.  The resourceType contains more information about the source of this searchMatch.
 *
 * @since 2.0
 */
public interface SearchMatch
{
    /**
     * Absolute URL to reach this search match.
     *
     * @return Absolute URL to reach this search match.
     */
    String getUrl();

    /**
     * Title of the search match
     *
     * @return Title of the search match
     */
    String getTitle();

    /**
     * An excerpt of the search match.  For example this could be a summary of the Wiki page.
     *
     * @return excerpt of the search match. May be null.
     */
    String getExcerpt();

    /**
     * Contains more information about the source of this match.
     *
     * @return The source resourceType.
     */
    ResourceType getResourceType();

}
