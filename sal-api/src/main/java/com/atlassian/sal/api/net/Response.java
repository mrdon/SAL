package com.atlassian.sal.api.net;

import java.io.IOException;
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
	 * @throws IOException
	 */
	String getResponseBodyAsString() throws IOException;

	/**
	 * @return the response body of the request.
	 * @throws IOException
	 */
	InputStream getResponseBodyAsStream() throws IOException;

	/**
	 * @return status test of the response
	 */
	String getStatusText();

	/**
	 * @return true if network returned OK
	 */
	boolean isSuccessful();
}
