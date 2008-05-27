package com.atlassian.sal.api.net;




/**
 * Callback interface used by {@link Request#execute(ResponseHandler)} method. Implementation of this interface performs actual handling of the response.
 */
public interface ResponseHandler
{
	/**
	 * Triggered when response from {@link Request#execute(ResponseHandler)} method becomes available. Implementations of this method should handle the response.
	 * 
	 * @param response a response object. Never null.
	 * @throws ResponseException
	 */
	void handle(Response response) throws ResponseException;
}
