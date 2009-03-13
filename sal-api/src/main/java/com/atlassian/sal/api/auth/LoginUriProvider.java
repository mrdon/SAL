package com.atlassian.sal.api.auth;

import java.net.URI;

/**
 * Provides the {@code URI} for the the OAuth plugin will need to redirect users to for them to login before they can
 * authorize consumer requests to access their data.
 */
public interface LoginUriProvider
{
    /**
     * Returns the {@code URI} the OAuth plugin can redirect users to for login.  It must append the {@code returnUri}
     * so that once login is complete, the user will be redirected back to the OAuth plugins authorization page.
     *  
     * @param returnUri {@code URI} of the page the application should redirect the user to after login is complete 
     * @return the {@code URI} the OAuth plugin can redirect users to for login
     */
    URI getLoginUri(URI returnUri);
}
