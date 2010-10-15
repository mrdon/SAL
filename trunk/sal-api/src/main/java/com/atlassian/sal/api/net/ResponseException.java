package com.atlassian.sal.api.net;

/**
 * This is exception is thrown by {@link Request#execute()} and {@link ResponseHandler#handle(Response)}. Acts as a
 * wrapper for any IOException thrown when executing the request. It also a root of user-defined exceptions thrown in
 * {@link ResponseHandler#handle(Response)} method.
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
