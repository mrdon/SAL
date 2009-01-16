package com.atlassian.sal.rest.user;

import com.atlassian.sal.api.user.User;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "user")
public class RestUser implements User
{
    @XmlElement
    private String username;

    @XmlElement
    private String emailAddress;

    @XmlElement
    private String password;

    @XmlElement
    private String firstName;

    @XmlElement
    private String lastName;

    RestUser()
    {
    }

    public RestUser(User user)
    {
        this.username = user.getUsername();
        this.emailAddress = user.getEmailAddress();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.password = user.getPassword();
    }

    public static User newUser(String username, String firstName, String lastName, String emailAddress)
    {
        return newUser(username, firstName, lastName, emailAddress, null);
    }

    public static User newUser(String username, String firstName, String lastName, String emailAddress, String password)
    {
        RestUser user = new RestUser();
        user.username = username;
        user.firstName = firstName;
        user.lastName = lastName;
        user.emailAddress = emailAddress;
        user.password = password;

        return user;
    }

    public String getUsername()
    {
        return username;
    }

    public String getEmailAddress()
    {
        return emailAddress;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public String getPassword()
    {
        return password;
    }
}
