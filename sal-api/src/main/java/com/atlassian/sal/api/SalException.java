package com.atlassian.sal.api;

public class SalException extends RuntimeException
{
    public SalException()
    {
        super();
    }

    public SalException(String message)
    {
        super(message);
    }

    public SalException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public SalException(Throwable cause)
    {
        super(cause);
    }
}
