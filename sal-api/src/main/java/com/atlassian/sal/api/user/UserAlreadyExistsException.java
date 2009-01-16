package com.atlassian.sal.api.user;

import com.atlassian.sal.api.SalException;

public class UserAlreadyExistsException extends SalException
{
    private final String username;

    public UserAlreadyExistsException(String username)
    {
        this.username = username;
    }

    public UserAlreadyExistsException(String username, Throwable cause)
    {
        super(cause);
        this.username = username;
    }

    @Override
    public String getMessage()
    {
        return "A user with that username (" + username + ") already exists.";
    }

    public String getUsername()
    {
        return username;
    }
}
