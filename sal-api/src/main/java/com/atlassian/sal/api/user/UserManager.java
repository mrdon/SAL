package com.atlassian.sal.api.user;

/**
 * Interface providing user based operations across various apps.
 */
public interface UserManager
{
    /**
     * Returns the username of the currently logged in user or null if no user can be found.
     * @return The user name of the logged in user or null
     */
    String getRemoteUsername();

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
}
