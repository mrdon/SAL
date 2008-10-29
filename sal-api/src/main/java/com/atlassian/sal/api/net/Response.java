package com.atlassian.sal.api.net;

import java.io.InputStream;

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
}
