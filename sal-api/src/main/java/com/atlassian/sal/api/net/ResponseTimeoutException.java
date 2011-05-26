package com.atlassian.sal.api.net;

/**
 * Thrown by {@link Request} methods to indicate that the request failed because the
 * server did not response in time.  This may be a {@link ResponseConnectTimeoutException}
 * or a {@link ResponseReadTimeoutException}.
 * 
 * @since 2.7.0
 */
public class ResponseTimeoutException extends ResponseException
{
    public ResponseTimeoutException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ResponseTimeoutException(String message)
    {
        super(message);
    }

    public ResponseTimeoutException(Throwable cause)
    {
        super(cause);
    }
}
