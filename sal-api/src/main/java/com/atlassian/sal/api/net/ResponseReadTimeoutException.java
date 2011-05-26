package com.atlassian.sal.api.net;

/**
 * Thrown by {@link Request} methods to indicate that the request failed because the
 * server did not respond within the timeout interval after a connection was made.
 * 
 * @since 2.7.0
 */
public class ResponseReadTimeoutException extends ResponseTimeoutException
{
    public ResponseReadTimeoutException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ResponseReadTimeoutException(String message)
    {
        super(message);
    }

    public ResponseReadTimeoutException(Throwable cause)
    {
        super(cause);
    }
}
