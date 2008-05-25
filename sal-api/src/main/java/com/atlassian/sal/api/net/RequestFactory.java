package com.atlassian.sal.api.net;

import java.net.URISyntaxException;

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
	 * @throws URISyntaxException
	 */
	T createRequest(MethodType methodType, String url) throws URISyntaxException;
}
