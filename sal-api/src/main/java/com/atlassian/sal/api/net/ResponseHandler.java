package com.atlassian.sal.api.net;


/**
 * Callback interface used by {@link Request#execute(ResponseHandler)} method. Implementation of this interface performs
 * actual handling of the response.
 *
 * @since 2.0
 */
public interface ResponseHandler
{
    /**
     * Triggered when response from {@link Request#execute(ResponseHandler)} method becomes available. Implementations
     * of this method should handle the response.
     *
     * @param response a response object. Never null.
     * @throws ResponseException If the response cannot be retrieved
     */
    void handle(Response response) throws ResponseException;
}
