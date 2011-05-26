package com.atlassian.sal.api.net;

/**
 * Thrown by {@link Request#execute()} to indicate that the request was unsuccessful
 * based on the response's status code.
 * <p>
 * For other Request methods that take a response handler, this exception is not thrown
 * since it is up to the handler to determine whether a response was an error.
 * 
 * @since 2.7.0
 */
public class ResponseStatusException extends ResponseException
{
    private final Response response;
    
    public ResponseStatusException(String message, Response response)
    {
        super(message);
        this.response = response;
    }

    /**
     * Returns the actual response, allowing further inspection of the status code
     * and error message.
     */
    public Response getResponse()
    {
        return response;
    }
}
