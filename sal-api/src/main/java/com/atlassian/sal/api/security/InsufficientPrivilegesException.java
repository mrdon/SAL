package com.atlassian.sal.api.security;

import com.atlassian.sal.api.SalException;

public class InsufficientPrivilegesException extends SalException
{
    public InsufficientPrivilegesException()
    {
        super();
    }

    public InsufficientPrivilegesException(String message)
    {
        super(message);
    }

    public InsufficientPrivilegesException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public InsufficientPrivilegesException(Throwable cause)
    {
        super(cause);
    }
}
