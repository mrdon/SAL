package com.atlassian.sal.api.search.query;


/**
 * Utility class to help with creating a query string for the
 * {@link com.atlassian.sal.api.search.SearchProvider#search(String)} method.
 * <p/>
 * Query strings will have the form:
 * <searchString>&<param1>=<value1>&<param2>=<value2>...
 *
 * @see com.atlassian.sal.api.search.query.SearchQueryParser
 */
public interface SearchQuery
{
    public static final String PARAMETER_SEPARATOR = "&";

    /**
     * Sets an arbitrary search parameter to the query string. If parameter with given name
     * exists, it will be overriden
     *
     * @param searchParameter the search parameter to add
     * @return a reference to this query creator
     */
    SearchQuery setParameter(String name, String value);
    
	/**
	 * Returns value of the parameter
	 * 
	 * @param project
	 * @return
	 */
	String getParameter(String name);

    /**
     * Appends string query to current query object. New parameters in query will override old ones.
     * 
     * @param query un-encoded query
     * @return
     */
    SearchQuery append(String query);

    
	/**
	 * Builds a url-encoded queryString to use with the {@link com.atlassian.sal.api.search.SearchProvider#search(String)}
	 * method. <code>queryString</code> consists of searchString and parameters.
	 *
	 * @return queryString created by this SearchQuery.
	 */
	String buildQueryString();

	/**
	 * Returns original string that user is searching for. Same as {@link #buildQueryString()} without parameters and
     * not url-encoded
	 * 
	 * @return
	 */
	String getSearchString();

	/**
	 * Convenient method to return integer value of parameter. If parameter does not exist, or is not parsable as Integer it returns <code>defaultValue</code>.
	 * 
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	int getParameter(String name, int defaultValue);
	
	
}
