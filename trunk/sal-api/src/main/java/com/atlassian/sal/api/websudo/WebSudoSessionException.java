package com.atlassian.sal.api.websudo;

/**
 * Thrown if there is a problem with the WebSudo session (missing or expired) for a request that requires WebSudo.
 *
 * @since 2.2
 */
public class WebSudoSessionException extends RuntimeException
{
    public WebSudoSessionException()
    {
        super();
    }

    public WebSudoSessionException(String message)
    {
        super(message);
    }

    public WebSudoSessionException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
