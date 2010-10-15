package com.atlassian.sal.api.search.query;


/**
 * Utility class to help with creating a query string for the
 * {@link com.atlassian.sal.api.search.SearchProvider#search(String,String)} method.
 * <p/>
 * Query strings will have the form:
 * <searchString>&<param1>=<value1>&<param2>=<value2>...
 *
 * @see com.atlassian.sal.api.search.query.SearchQueryParser
 * @since 2.0
 */
public interface SearchQuery
{
    /**
     * The parameter separator value
     */
    public static final String PARAMETER_SEPARATOR = "&";

    /**
     * Sets an arbitrary search parameter to the query string. If parameter with given name
     * exists, it will be overriden
     *
     * @param name  the search parameter name
     * @param value the search parameter value
     * @return a reference to this query creator
     */
    SearchQuery setParameter(String name, String value);

    /**
     * Returns value of the parameter
     *
     * @param name the parameter name
     * @return the parameter value
     */
    String getParameter(String name);

    /**
     * Appends string query to current query object. New parameters in query will override old ones.
     *
     * @param query un-encoded query
     * @return the created and parsed search query
     */
    SearchQuery append(String query);


    /**
     * Builds a url-encoded queryString to use with the
     * {@link com.atlassian.sal.api.search.SearchProvider#search(String,String)} method. <code>queryString</code>
     * consists of searchString and parameters.
     *
     * @return queryString created by this SearchQuery.
     */
    String buildQueryString();

    /**
     * @return the original string that user is searching for. Same as {@link #buildQueryString()} without parameters and
     *         not url-encoded
     */
    String getSearchString();

    /**
     * Convenient method to return integer value of parameter. If parameter does not exist, or is not parsable as Integer it returns <code>defaultValue</code>.
     *
     * @param name         The parameter name
     * @param defaultValue The default value if that parameter is not specified
     * @return the integer value
     */
	int getParameter(String name, int defaultValue);
	
	
}
