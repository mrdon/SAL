package com.atlassian.sal.api.http.httpclient;

/**
 * For times we want to return an authenticator that does nothing. For example, TrustedApp calls break noisily when
 * you send them as an anonymous user, so why not just not authenticate at all?
 */
public class NullAuthenticator extends HttpClientAuthenticator
{
}
