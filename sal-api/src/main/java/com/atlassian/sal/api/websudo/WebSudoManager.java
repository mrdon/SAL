package com.atlassian.sal.api.websudo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Allows the client to request WebSudo protection from the host application.
 * <p/>
 * <p/>
 * Usage pattern:
 * <pre>
 *  @Override
 * public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
 * {
 *      if(webSudoManager.isWebSudoProtected(request)) {
 *          // do something
 *      } else {
 *          webSudoManager.enforceWebSudoProtection(request, response);
 *      }
 * }
 * </pre>
 *
 * @since 2.2
 */
public interface WebSudoManager
{

    /**
     * Check whether this request is protected by a WebSudo session.
     * <p/> Calling this method has no side effects.
     *
     * @param request the current {@link HttpServletRequest}
     * @return {@code true} if this request is protected by a WebSudo session, {@code false} otherwise.
     */
    boolean isWebSudoProtected(HttpServletRequest request);

    /**
     * Ensure that the current request is protected by a WebSudo session. Typically this will result in a redirect
     * to a WebSudo form which in turn redirects to the original request.
     * <p/>
     * This is a no op if this request is already
     * protected by a WebSudo session (i.e. {@link #isWebSudoProtected(javax.servlet.http.HttpServletRequest)} would return true).
     *
     * @param request  the current {@link HttpServletRequest}
     * @param response the current {@link HttpServletResponse}
     */
    void enforceWebSudoProtection(HttpServletRequest request, HttpServletResponse response);
}
