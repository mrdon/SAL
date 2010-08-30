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
     *
     * @return the username of the user associated with this profile information
     */
    String getUsername();

    /**
     * Returns the full name of the user associated with this profile information
     *
     * @return the full name of the user associated with this profile information,
     * or {@code null} if a full name was not provided or the application does not
     * support the full name as profile data
     */
    String getFullName();

    /**
     * Returns the email address of the user associated with this profile
     *
     * @return the email address of the user associated with this profile,
     * or {@code null} if an email address was not provided or the application does
     * not support email addresses as profile data
     */
    String getEmail();

    /**
     * Returns a URI for the user's profile picture. The returned URI will point
     * to an image of the user's profile picture no smaller than the requested size.
     *
     * The URI will either be relative to the application's base URI, or absolute if
     * the profile picture is being served by an external server
     *
     * @param width the preferred width of the desired picture
     * @param height the preferred height of the desired picture
     * @return a URI pointing to an image of the user's profile picture, or {@code null}
     * if a profile picture was not provided, the application does not support
     * profile pictures as profile data, or the application was unable to provide an
     * image larger than or equal to the requested size
     */
    URI getProfilePictureUri(int width, int height);

    /**
     * Returns a URI for the user's profile picture. The returned URI will point
     * to the largest possible unscaled image of the user's profile picture that the application
     * can provide
     *
     * The URI will either be relative to the application's base URI, or absolute if
     * the profile picture is being served by an external server
     *
     * @return a URI pointing to an image of the user's profile picture, or {@code null}
     * if a profile picture was not provided or the application does not support
     * profile pictures as profile data
     */
    URI getProfilePictureUri();

    /**
     * Returns a URI for the user's profile page. The URI will be relative to
     * the application's base URI
     * 
     * @return a relative URI pointing to the user's profile page, or {@code null} if
     * the user does not have a profile page or the application does not support profile
     * pages
     */
    URI getProfilePageUri();
}
