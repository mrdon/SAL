package com.atlassian.sal.api.xsrf;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Gives access to the applications XSRF tokens
 *
 * @since 2.4
 */
public interface XsrfTokenAccessor
{
    /**
     * Get the persistent token for the given request, that is, the token stored in the users session or in a cookie,
     * not the token submitted as part of a form.
     *
     * @param request The request to get the token from
     * @param response The response to add the cookie to if necessary
     * @param create Whether a new token should be created if there is none in the request.  The new token should be
     *               persistent across subsequent requests, ie, added to the users session or a cookie.
     * @return The token for the request, or null if no token was found and create was false
     */
    String getXsrfToken(HttpServletRequest request, HttpServletResponse response, boolean create);
}
