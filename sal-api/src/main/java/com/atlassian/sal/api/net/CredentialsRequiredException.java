package com.atlassian.sal.api.net;

/**
 * <p>
 * Thrown by {@link com.atlassian.sal.api.net.Request#execute()} when the
 * endpoint requires authentication.
 * </p>
 *
 * @since   2.1.0
 */
public class CredentialsRequiredException extends ResponseException
{
    private final String url;

    public CredentialsRequiredException(Throwable cause, String url)
    {
        super(cause);
        this.url = url;
    }

    public CredentialsRequiredException(String message, String url)
    {
        super(message);
        this.url = url;
    }

    public CredentialsRequiredException(String message, Throwable cause, String url)
    {
        super(message, cause);
        this.url = url;
    }

    /**
     * <p>
     * The URL that can be used to provide authentication for the requested
     * resource.
     * </p>
     * <p>
     * The typical scenario is a call to an OAuth-protected remote resource for
     * which the caller does not have an access token. If the caller has the
     * ability to send a redirect (in case of a plugin servlet or webwork
     * action), it would do so using this URL. This URL will take the user to a
     * local endpoint that will perform the "OAuth dance":
     * <li>request a Request Token from the remote OAuth provider</li>
     * <li>redirect the client to the provider's authorize URL, using itself
     * for the callback</li>
     * <li>on successful approval by the user, swap the request token for an
     * access token</li>
     * <li>redirect the user back to original resource (the plugin's servlet
     * or action)</li>
     * </p>
     * <p>
     * If the caller does not have the ability to perform an HTTP redirect to
     * this URL (possibly because it's a Web Panel), it can display a link or
     * button that will open the URL in a popup dialog with an iframe, allowing
     * the user to perform the oauth dance at a later time.
     * </p>
     *
     * @return  the URL that can be used to provide authentication for the
     * requested resource.
     */
    public String getUrl()
    {
        return url;
    }
}
