package com.atlassian.sal.api.net;

/**
 * Thrown by {@link Request} methods to indicate that the request failed because the
 * server did not comply with the request protocol.
 * <p>
 * For HTTP requests, this includes conditions such as the server returning an invalid
 * HTTP response or a circular redirect.
 * 
 * @since 2.7.0
 */
public class ResponseProtocolException extends ResponseException
{
    public ResponseProtocolException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ResponseProtocolException(String message)
    {
        super(message);
    }

    public ResponseProtocolException(Throwable cause)
    {
        super(cause);
    }
}
