package com.atlassian.sal.api.websudo;

/**
 * 
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
