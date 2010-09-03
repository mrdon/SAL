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
 *      try {
 *          webSudoManager.willExecuteWebSudoRequest(request);
 *          // do something
 *      } catch(WebSudoSessionException wes) {
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
     * Check whether this request can be executed. This checks if the request is already part of
     * a WebSudo session or if WebSudo is enabled at all.
     * <p/> Calling this method has no side effects.
     *
     * @param request the current {@link HttpServletRequest}
     * @return {@code true} if this request is protected by a WebSudo session or WebSudo is disabled, {@code false} otherwise.
     */
    boolean canExecuteRequest(HttpServletRequest request);

    /**
     * Ensure that the current request is protected by a WebSudo session. Typically this will result in a redirect
     * to a WebSudo form which in turn redirects to the original request.
     * <p/>
     * This is a no op if this request is already
     * protected by a WebSudo session (i.e. {@link #canExecuteRequest(javax.servlet.http.HttpServletRequest)} would return true).
     *
     * @param request  the current {@link HttpServletRequest}
     * @param response the current {@link HttpServletResponse}
     */
    void enforceWebSudoProtection(HttpServletRequest request, HttpServletResponse response);

    /**
     * Mark the current request as a request for a WebSudo protected resource.
     * <p/>
     * Throws a {@link WebSudoSessionException} if the current {@code request} is not protected by WebSudo.
     * <p/>
     * This notifies the host application that the {@code request} is a request for a WebSudp protected resource.  
     *
     * @param request  the current {@link HttpServletRequest}
     * @throws WebSudoSessionException if the current {@code request} is not protected by WebSudo.
     * @since 2.2.0-beta10
     */
    void willExecuteWebSudoRequest(HttpServletRequest request) throws WebSudoSessionException;
}
