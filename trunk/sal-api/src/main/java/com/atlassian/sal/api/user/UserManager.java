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
     * Returns a {@code UserProfile object} for the specified user or null if no user can be found
     * @param username The username of the user whose profile is requested
     * @return The user's profile or null
     * @since 2.2.0
     */
    UserProfile getUserProfile(String username);

    /**
     * Returns whether the user is in the specify group
     *
     * @param username The username to check
     * @param group    The group to check
     * @return {@code true} if the user is in the specified group
     */
    boolean isUserInGroup(String username, String group);

    /**
     * Returns {@code true} or {@code false} depending on whether a user has been granted the system administrator
     * permission. A system administrator has full administrative permissions in the application, including permission
     * to perform operations that may affect the underlying operating system, such as specifying filesystem paths,
     * installing plugins, configuring mail servers and logging, performing backups and restores, etc. Only check for
     * system administrator when performing this type of operation. Operations that do not affect the underlying system
     * should use {@link #isAdmin(String)} instead.
     *
     * @param username The username of the user to check
     * @return {@code true} or {@code false} depending on whether a user has been granted the system admin permission.
     * @see <a href="http://confluence.atlassian.com/display/JIRA/Managing+Global+Permissions#ManagingGlobalPermissions-About%27JIRASystemAdministrators%27and%27JIRAAdministrators%27">About 'JIRA System Administrators' and 'JIRA Administrators'</a>
     * @see <a href="http://confluence.atlassian.com/display/DOC/Global+Permissions+Overview#GlobalPermissionsOverview-confluenceadmin">Comparing the System Administrator with the Confluence Administrator Permission</a>
     */
    boolean isSystemAdmin(String username);

    /**
     * Returns {@code true} or {@code false} depending on whether a user has been granted the administrator permission.
     * An administrator may have restricted administrative permissions that only apply to application-level
     * configuration that cannot affect the underlying operating system. Only check for administrator permission when
     * performing this type of operation. Operations that can affect security, the filesystem, or allow arbitrary code
     * execution must check {@link #isSystemAdmin(String)} instead.
     * <p/>
     * Note that system administrator permission implies administrator permission. That is, any username for which
     * {@code userManager.isSystemAdmin(username)} returns {@code true} will also return {@code true} for
     * {@code userManager.isAdmin(username)}.
     * 
     * @param username The username of the user to check
     * @return {@code true} or {@code false} depending on whether the user has been granted the admin permission
     * @see <a href="http://confluence.atlassian.com/display/JIRA/Managing+Global+Permissions#ManagingGlobalPermissions-About%27JIRASystemAdministrators%27and%27JIRAAdministrators%27">About 'JIRA System Administrators' and 'JIRA Administrators'</a>
     * @see <a href="http://confluence.atlassian.com/display/DOC/Global+Permissions+Overview#GlobalPermissionsOverview-confluenceadmin">Comparing the System Administrator with the Confluence Administrator Permission</a>
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
