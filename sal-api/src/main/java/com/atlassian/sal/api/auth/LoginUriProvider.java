package com.atlassian.sal.api.auth;

import java.net.URI;

/**
 * Provides the {@code URI} to redirect users to for them to login before they can
 * authorize consumer requests to access their data.
 *
 * @since 2.0
 */
public interface LoginUriProvider
{
    /**
     * Returns the {@code URI} to redirect users for login.  It must append the {@code returnUri}
     * so that once login is complete, the user will be redirected back to the original page.
     *
     * @param returnUri {@code URI} of the page the application should redirect the user to after login is complete
     * @return the {@code URI} to redirect users for login
     */
    URI getLoginUri(URI returnUri);
}
