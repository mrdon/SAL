package com.atlassian.sal.api.net;

/**
 * Thrown by {@link Request} methods to indicate that the request failed because a
 * connection could not be established within the timeout interval.
 * 
 * @since 2.7.0
 */
public class ResponseConnectTimeoutException extends ResponseTimeoutException
{
    public ResponseConnectTimeoutException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ResponseConnectTimeoutException(String message)
    {
        super(message);
    }

    public ResponseConnectTimeoutException(Throwable cause)
    {
        super(cause);
    }
}
