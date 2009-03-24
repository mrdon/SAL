package com.atlassian.sal.api.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Allows the underlying framework to take some actions on authentication events.
 *
 * @since 2.0.0
 */
public interface AuthenticationListener
{
    /**
     * Called when the signature is validated and the user is resolved and permissions are verified.  Responsible
     * for preparing the HTTP request or session such that the application sees the user as logged in for the rest of
     * this request.
     *
     * @param result Authentication result containing the user
     * @param request Current HTTP request being processed
     * @param response HTTP response for the current request, provided so the application can set any headers it might
     *                 need set
     */
    void authenticationSuccess(Authenticator.Result result, HttpServletRequest request, HttpServletResponse response);

    /**
     * Called when the signature cannot be validated or the user cannot be resolved or does not have permission
     * to access the resource.
     *
     * @param result Authentication result containing the details of the failure
     * @param request Current HTTP request being processed
     * @param response HTTP response for the current request, provided so the application can set any headers it might
     *                 need set
     */
    void authenticationFailure(Authenticator.Result result, HttpServletRequest request, HttpServletResponse response);

    /**
     * Called when there is a failure in trying to process the request, such as an IO failure.
     *
     * @param result Authentication result containing the details of the error
     * @param request Current HTTP request being processed
     * @param response HTTP response for the current request, provided so the application can set any headers it might
     *                 need set
     */
    void authenticationError(Authenticator.Result result, HttpServletRequest request, HttpServletResponse response);

    /**
     * Called if it was determined that authentication should not be attempted, usually because the
     * {@link AuthenticationController#shouldAttemptAuthentication(HttpServletRequest)} returned {@code false}.
     *
     * @param request Current HTTP request being processed
     * @param response HTTP response for the current request, provided so the application can set any headers it might
     *                 need set
     */
    void authenticationNotAttempted(HttpServletRequest request, HttpServletResponse response);
}
