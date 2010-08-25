package com.atlassian.sal.api.user;

import java.net.URI;

/**
 * Interface encapsulating a user's profile information. Any of the properties except
 * for the username may be {@code null}, which indicates either that the underlying application
 * does not support that profile data, or that the user did not provide that profile data.
 * 
 * @since 2.2.0
 */
public interface UserProfile
{
    /**
     * Returns the username of the user associated with this profile information
     * @return the username of the user associated with this profile information
     */
    String getUsername();

    /**
     * Returns the full name of the user associated with this profile information
     * @return the full name of the user associated with this profile information
     */
    String getFullName();

    /**
     * Returns the email address of the user associated with this profile
     * @return the email address of the user associated with this profile
     */
    String getEmail();

    /**
     * Returns a URI for the user's profile picture. The returned URI will point
     * to an image of the user's profile picture of an appropriate size; however
     * the specified size should be assumed to be a hint that the application
     * may or may not respect.
     *
     * The URI will be relative to the application's base URI
     *
     * @param width a hint indicating the width of the desired picture
     * @param height a hint indicating the height of the desired picture
     * @return a relative URI pointing to an image of the user's profile picture
     */
    URI getProfilePictureUri(int width, int height);

    /**
     * Returns a URI for the user's profile page. The URI will be relative to
     * the application's base URI
     * @return a relative URI pointing to the user's profile page
     */
    URI getProfilePageUri();
}
