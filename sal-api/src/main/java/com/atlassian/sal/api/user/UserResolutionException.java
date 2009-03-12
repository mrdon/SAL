package com.atlassian.sal.api.user;

/**
 * Thrown if there is a problem when trying to resolve a username to a user, such as a failure in accessing an external
 * user store.
 */
public class UserResolutionException extends RuntimeException
{
    public UserResolutionException(final String messge, final Throwable cause)
    {
        super(messge, cause);
    }

    public UserResolutionException(final Throwable cause)
    {
        super(cause);
    }

    public UserResolutionException(final String message)
    {
        super(message);
    }
}
