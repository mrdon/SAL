package com.atlassian.sal.api.http.httpclient;

import com.atlassian.sal.api.http.HttpRequest;

import org.apache.commons.httpclient.HttpMethod;

/**
 * Default authenticator which adds query parameters to a url used in an http request
 */
final class DefaultSeraphAuthenticator extends HttpClientAuthenticator
{

    /**
     * Default constructor, declared package private to force use of factory method
     */
    DefaultSeraphAuthenticator()
    {
        super();
    }

    /**
     * Generate an <code>HttpMethod</code> with URL of the form <code>http://example.com/?os_username=johnsmith&os_password=john'spassword</code>
     *
     * @param request the data containing information (such as method type) about the request that will be issued
     * @return an HttpMethod which contains the information relevant to
     */
    public final HttpMethod makeMethod(HttpRequest request)
    {
        StringBuffer buf = new StringBuffer(request.getUrl());

        String url = request.getUrl();

        // Make sure someone hasn't set the username manually... abort if it's there.
        if (url.indexOf("os_username") < 0)
        {
            if (url.indexOf('?') < 0)
                buf.append('?');
            else if (!url.endsWith("&"))
                buf.append('&');

            buf.append("os_username=").append(getProperty("username")).append("&os_password=").append(getProperty("password"));
        }
        return HttpClientHttpRequest.createMethod(buf.toString(), request);
    }

}
