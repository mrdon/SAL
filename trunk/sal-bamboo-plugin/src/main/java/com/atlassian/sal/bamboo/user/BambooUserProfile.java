package com.atlassian.sal.bamboo.user;

import java.net.URI;

import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.user.User;

/**
 * A simple implementation of the {@link UserProfile} interface for Bamboo
 *
 * @since 2.2.0
 */
public final class BambooUserProfile implements UserProfile
{
    private final String username;
    private final String fullname;
    private final String email;

    public BambooUserProfile(User user)
    {
        this.username = user.getName();
        this.fullname = user.getFullName();
        this.email = user.getEmail();
    }

    public String getUsername()
    {
        return username;
    }

    public String getFullName()
    {
        return fullname;
    }

    public String getEmail()
    {
        return email;
    }

    public URI getProfilePictureUri(int width, int height)
    {
        // Bamboo does not support user profile pictures
        return null;
    }

    public URI getProfilePictureUri()
    {
        // Bamboo does not support user profile pictures
        return null;
    }

    public URI getProfilePageUri()
    {
        return URI.create("/browse/user/" + username);
    }
}
