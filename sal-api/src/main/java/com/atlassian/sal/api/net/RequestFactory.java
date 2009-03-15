package com.atlassian.sal.api.net;

import com.atlassian.sal.api.net.Request.MethodType;

/**
 * Factory to create {@link Request}s. Requests are used to make network calls.
 * @param <T>
 */
public interface RequestFactory<T extends Request<?>>
{
	/**
	 * Creates a request of given {@link MethodType} to given url
	 * @param methodType
	 * @param url
	 * @return
	 */
	T createRequest(MethodType methodType, String url);
	
	/**
	 * Indicates whether the requests can support headers
	 * @return true if the requests can support headers
	 * @see Request#setHeader(String, String)
	 * @see Request#addHeader(String, String)
	 */
	boolean supportsHeader();
}
