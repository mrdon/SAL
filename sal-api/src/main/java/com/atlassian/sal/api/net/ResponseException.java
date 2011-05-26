package com.atlassian.sal.api.net;

/**
 * This exception is thrown by {@link Request#execute()} and {@link ResponseHandler#handle(Response)}. Acts as a
 * wrapper for any IOException thrown when executing the request. It also a root of user-defined exceptions thrown in
 * {@link ResponseHandler#handle(Response)} method.
 * <p>
 * Subclasses of this class such as {@link ResponseConnectTimeoutException},  {@link ResponseProtocolException},
 * and {@link ResponseTransportException} may be thrown for specific error conditions; you are encouraged to
 * catch these if you need to handle them specifically, rather than checking {@link #getCause()} to detect
 * the corresponding exceptions thrown by the internal client implementation.
 *
 * @since 2.0
 */
public class ResponseException extends Exception
{

    public ResponseException()
    {
        super();
    }

    public ResponseException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ResponseException(String message)
    {
        super(message);
    }

    public ResponseException(Throwable cause)
    {
        super(cause);
    }

}
