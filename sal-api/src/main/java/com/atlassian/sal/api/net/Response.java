package com.atlassian.sal.api.net;

import java.io.InputStream;
import java.util.Map;

/**
 * Represents the response when calling {@link Request#execute(ResponseHandler)}
 */
public interface Response
{
	/**
	 * @return status code of the request.
	 */
	int getStatusCode();

	/**
	 * @return the response body of the request.
	 * @throws ResponseException
	 */
	String getResponseBodyAsString() throws ResponseException;

	/**
	 * @return the response body of the request.
	 * @throws ResponseException
	 */
	InputStream getResponseBodyAsStream() throws ResponseException;

	/**
	 * @return status test of the response
	 */
	String getStatusText();

	/**
	 * @return true if network returned a status code in the 200 range or 300 range
	 */
	boolean isSuccessful();

    /**
     * Get's the header by the given name
     *
     * @param name The name of the header
     * @return The value of the header.  This will include all values, comma seperated, if multiple header fields with
     * the given name were specified, as per RFC2616.
     */
    String getHeader(String name);

    /**
     * Get a map of all the headers
     * @return A map of header names to header values.  The header values will include all values, comma seperated, if
     * multiple header fields with the given name were specified, as per RFC2616.
     */
    Map<String, String> getHeaders();
}
