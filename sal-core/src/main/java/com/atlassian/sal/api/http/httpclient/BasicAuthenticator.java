package com.atlassian.sal.api.http.httpclient;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.log4j.Category;

/**
 * A simple authenticator which modifies the <code>HttpClient</code>'s state to provide credentials
 */
final class BasicAuthenticator extends HttpClientAuthenticator
{

    /**
     * Default constructr, declared package private to force use of factory method
     */
    BasicAuthenticator()
    {
        super();
    }

    /**
     * A logger used to log error messages
     */
    private static final Category log = Category.getInstance(BasicAuthenticator.class);

    /**
     * Performs the preprocessing for the supplied client. This method sets the authentication to be preemptive and sets
     * the credentials using the supplied <code>HttpMethod</code> and the currently stored username and password.
     *
     * @param client the <code>HttpClient</code> whose state will be affected
     * @param method the <code>HttpMethod</code> whose URI and host will be used to modify the client
     */
    public final void preprocess(HttpClient client, HttpMethod method)
    {
        try
        {
            client.getState().setAuthenticationPreemptive(true);
            client.getState().setCredentials(null, method.getURI().getHost(), new UsernamePasswordCredentials(getProperty("username"), getProperty("password")));
        }
        catch (URIException e)
        {
            log.error("Unable to parse URI to set credentials: " + e.toString(), e);
        }
    }
}
