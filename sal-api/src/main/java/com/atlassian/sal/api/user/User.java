package com.atlassian.sal.api.user;

public interface User
{
    String getUsername();

    String getEmailAddress();

    String getFirstName();

    String getLastName();

    String getPassword();
}
