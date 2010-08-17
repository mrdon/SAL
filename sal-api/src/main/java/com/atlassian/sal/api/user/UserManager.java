package com.atlassian.sal.api.user;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

/**
 * Interface providing user based operations across various apps.
 *
 * @since 2.0
 */
public interface UserManager
{
    /**
     * Returns the username of the currently logged in user or null if no user can be found.  If possible, please use
     * {@link #getRemoteUsername(HttpServletRequest)}.
     *
     * @return The user name of the logged in user or null
     */
    String getRemoteUsername();

    /**
     * Returns the username of the currently logged in user or null if no user can be found.
     *
     * @param request The request to retrieve the username from
     * @return The user name of the logged in user or null
     */
    String getRemoteUsername(HttpServletRequest request);

    /**
     * Returns the full name of the currently logged in user or null if no user can be found.  If possible, please use
     * {@link #getRemoteUsername(HttpServletRequest)}.
     *
     * @return The full name of the logged in user or null
     * @since 2.2.0
     */
    String getRemoteUserFullname();

    /**
     * Returns the full name of the currently logged in user or null if no user can be found.
     *
     * @param request The request to retrieve the username from
     * @return The full name of the logged in user or null
     * @since 2.2.0
     */
    String getRemoteUserFullname(HttpServletRequest request);

    /**
     * Returns whether the user is in the specify group
     *
     * @param username The username to check
     * @param group    The group to check
     * @return {@code true} if the user is in the specified group
     */
    boolean isUserInGroup(String username, String group);

    /**
     * Returns {@code true} or {@code false} depending on whether a user has been granted the system admin permission.
     *
     * @param username The username of the user to check
     * @return {@code true} or {@code false} depending on whether a user has been granted the system admin permission.
     */
    boolean isSystemAdmin(String username);

    /**
     * Returns {@code true} or {@code false} depending on whether a user has been granted the admin permission
     * 
     * @param username The username of the user to check
     * @return {@code true} or {@code false} depending on whether the user has been granted the admin permission
     */
    boolean isAdmin(String username);

    /**
     * Given a usernamen & password, this method checks whether or not the provided user can
     * be authenticated
     *
     * @param username Username of the user
     * @param password Password of the user
     * @return {@code true} if the user can be authenticated, {@code false} otherwise
     */
    boolean authenticate(String username, String password);

    /**
     * Returns the user that made this request or {@code null} if this application does not have such a user.
     *
     * @param username Username of the user a consumer is making a request on behalf of
     * @return {@code Principal} corresponding to the username, {@code null} if the user does not exist
     * @throws UserResolutionException thrown if there is a problem resolving the user, such as a failure when accessing
     *                                 an external user store
     */
    Principal resolve(String username) throws UserResolutionException;
}
