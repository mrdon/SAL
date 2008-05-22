package com.atlassian.sal.api.http.httpclient;

import com.atlassian.sal.api.component.ComponentLocator;
import com.atlassian.sal.api.http.Authenticator;
import com.atlassian.sal.api.http.AuthenticatorFactory;
import com.atlassian.sal.api.trusted.CertificateFactory;
import com.atlassian.security.auth.trustedapps.EncryptedCertificate;

/**
 * A factory for <code>HttpClientAuthenticator</code>s. <code>HttpClientAuthenticator</code>s are used to create and
 * preprocess <code>HttpMethod</code>s.
 */
public final class HttpClientAuthenticatorFactory implements AuthenticatorFactory
{

    /**
     * Retrieves a simple authenticator for a specific user name and password
     *
     * @param username the user's name
     * @param password the user's password
     * @return a simple authenticator which modifies client credentials
     */
    public final Authenticator getBasicAuthenticator(String username, String password)
    {
        BasicAuthenticator authenticator = new BasicAuthenticator();

        authenticator.setProperty("username", username);
        authenticator.setProperty("password", password);

        return authenticator;
    }

    /**
     * Retrieves an authenticator which uses a certificate to provide authentication
     *
     * @param username the user's name
     * @return an authenticator which uses a certificate generated from the given username for authentication
     */
    public final Authenticator getTrustedTokenAuthenticator(String username)
    {
        if (username == null || "".equals(username))
            return new NullAuthenticator();

        CertificateFactory certificateFactory = ComponentLocator.getComponent(CertificateFactory.class);
        EncryptedCertificate certificate = certificateFactory.createCertificate(username);

        return new TrustedTokenAuthenticator(certificate);
    }

    /**
     * Retrieves a simple authenticator for a specific user name and password
     *
     * @param username the user's name
     * @param password the user's password
     * @return an authenticator which modifies a uri's query parameters to provide authentication
     */
    public final Authenticator getSeraphAuthenticator(String username, String password)
    {
        DefaultSeraphAuthenticator authenticator = new DefaultSeraphAuthenticator();

        authenticator.setProperty("username", username);
        authenticator.setProperty("password", password);

        return authenticator;
    }
}
