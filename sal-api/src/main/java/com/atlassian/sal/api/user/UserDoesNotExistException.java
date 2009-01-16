package com.atlassian.sal.api.user;

import com.atlassian.sal.api.SalException;

public class UserDoesNotExistException extends SalException
{
    private final String username;

    public UserDoesNotExistException(String username)
    {
        this.username = username;
    }

    public UserDoesNotExistException(String username, Throwable cause)
    {
        super(cause);
        this.username = username;
    }

    @Override
    public String getMessage()
    {
        return "Failed to find user with username <" + username + ">";
    }

    public String getUsername()
    {
        return username;
    }
}
