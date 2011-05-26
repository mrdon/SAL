package com.atlassian.sal.api.net;

/**
 * Thrown by {@link Request} methods to indicate that the request failed because of
 * an I/O error in the underlying protocol.
 * <p>
 * For HTTP requests, {@link #getCause()} will return the corresponding
 * {@link SocketException}.
 * 
 * @since 2.7.0
 */
public class ResponseTransportException extends ResponseException
{
    public ResponseTransportException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ResponseTransportException(String message)
    {
        super(message);
    }

    public ResponseTransportException(Throwable cause)
    {
        super(cause);
    }
}
