package com.atlassian.sal.api.http;

/**
 * Defines objects capable of generating an <code>Authenticator</code>. Authenticators store properties and typically
 * use this information to authenticate connections.
 */
public interface AuthenticatorFactory
{

    /**
     * Retrieves a simple authenticator for a specific user name and password
     *
     * @param username the user's name
     * @param password the user's password
     * @return a simple authenticator which modifies client credentials to provide authentication
     */
    Authenticator getBasicAuthenticator(String username, String password);

    /**
     * Retrieves an authenticator which uses a certificate to provide authentication
     *
     * @param username the user's name
     * @return an authenticator which uses the given username and certificate for authentication
     */
    Authenticator getTrustedTokenAuthenticator(String username);

    /**
     * Retrieves a simple authenticator for a specific user name and password
     *
     * @param username the user's name
     * @param password the user's password
     * @return an authenticator which modifies a uri's query parameters to provide authentication
     */
    Authenticator getSeraphAuthenticator(String username, String password);
    
}
