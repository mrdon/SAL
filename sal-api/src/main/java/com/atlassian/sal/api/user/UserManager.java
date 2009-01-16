package com.atlassian.sal.api.user;

import com.atlassian.sal.api.security.InsufficientPrivilegesException;

import javax.servlet.http.HttpServletRequest;

/** Interface providing user based operations across various apps. */
public interface UserManager
{
    /**
     * Returns the username of the currently logged in user or null if no user can be found.
     * @return The user name of the logged in user or null
     * @deprecated use {@link getRemoteUsername(javax.servlet.ServletRequest)}
     */
    @Deprecated
    String getRemoteUsername();

    /**
     * Returns the username of the currently logged in user or null if no user can be found.
     * @param request The request to retrieve the username from
     * @return The user name of the logged in user or null
     */
    String getRemoteUsername(HttpServletRequest request);

    /**
     * Returns whether the user is in the specify group
     * @param username The username to check
     * @param group The group to check
     * @return True if the user is in the specified group
     */
    boolean isUserInGroup(String username, String group);

    /**
     * Returns true or false depending on if a user has been granted the system admin permission.
     * @param username The username of the user to check
     * @return true or false depending on if a user has been granted the system admin permission.
     */
    boolean isSystemAdmin(String username);

    /**
     * Given a usernamen & password, this method checks, whether or not the provided user can
     * be authenticated
     * @param username Username of the user
     * @param password Password of the user
     * @return True if the user can be authenticated, false otherwise
     */
    boolean authenticate(String username, String password);

    /**
     * Gets a user from the application
     * @param username the username of the user for retrieve
     * @return the user
     * @throws UserDoesNotExistException if the user cannot be found
     * @throws InsufficientPrivilegesException if the currently the authenticated user doesn't have enough privileges to view the user information.
     */
    User getUser(String username);

    /**
     * Creates a user into the given application.
     * @param user the information about the user to be created.
     * @return the created user. In most cases it will be different than the user parameter.
     * @throws UserAlreadyExistsException if a user with the given username already exists in the application.
     * @throws InsufficientPrivilegesException if the currently the authenticated user doesn't have enough privileges to create new users.
     */
    User createUser(User user);

    /**
     * Updates a user in the given application.
     * @param user the information about the user to be created. If a password has been set on the user it will be ignored.
     * @return the created user. In most cases it will be different than the user parameter.
     * @throws UserDoesNotExistException if the user cannot be found.
     * @throws InsufficientPrivilegesException if the currently the authenticated user doesn't have enough privileges to update the user.
     */
    User updateUser(User user);

    /**
     * Removes a user from the given application.
     * @param username the username of the user to be deleted.
     * @throws UserDoesNotExistException if the user cannot be found.
     * @throws InsufficientPrivilegesException if the currently the authenticated user doesn't have enough privileges to remove users.
     */
    void removeUser(String username);
}
