package com.atlassian.sal.api.net;

/**
 * Callback interface used by the {@link Request#executeAndReturn(ReturningResponseHandler)}
 * method. Implementation of this interface performs actual handling of the
 * response.
 * <p>
 * If no result will be produced, then instead of using this class, implement
 * {@link com.atlassian.sal.api.net.ResponseHandler} and pass it to
 * {@link Request#execute(ResponseHandler)}.
 *
 * @since   2.2
 */
public interface ReturningResponseHandler<T extends Response, R>
{
    /**
     * Triggered when response from {@link Request#executeAndReturn(ReturningResponseHandler)}
     * method becomes available. Implementations of this method should handle the response.
     *
     * @param response a response object. Never {@code null}.
     * @return the result produces by this handler.
     * @throws ResponseException If the response cannot be retrieved
     */
    R handle(T response) throws ResponseException;
}
