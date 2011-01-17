package com.atlassian.sal.api.xsrf;

import javax.servlet.http.HttpServletRequest;

/**
 * Verifies that a submitted token is valid.
 *
 * @since 2.4.
 */
public interface XsrfTokenValidator
{
    /**
     * Validate a form encoded token.
     * Will first read the token from the cookie and then validate
     *
     * @param request the request that contains the token.
     *
     * @return true if the token in the request matches the one in the cookie
     */
    boolean validateFormEncodedToken(HttpServletRequest request);

}
