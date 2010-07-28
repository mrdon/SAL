package com.atlassian.sal.api.net;

/**
 * @since   v2.2.0
 */
public interface ReturningResponseHandler<T extends Response, R>
{
    /**
     * Triggered when response from {@link Request#executeAndReturn(ReturningResponseHandler)}
     * method becomes available. Implementations of this method should handle the response.
     *
     * @param response a response object. Never {@code null}.
     * @throws ResponseException If the response cannot be retrieved
     */
    R handle(T response) throws ResponseException;
}
