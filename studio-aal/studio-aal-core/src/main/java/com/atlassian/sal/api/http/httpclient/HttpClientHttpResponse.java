package com.atlassian.sal.api.http.httpclient;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpMethod;

import com.atlassian.sal.api.http.HttpRequest;
import com.atlassian.sal.api.http.HttpResponse;
import com.atlassian.sal.api.http.SizeLimitedInputStream;

/**
 * A basic implementation of <code>HttpResponse</code>.
 */
public final class HttpClientHttpResponse implements HttpResponse
{
    private final HttpRequest request;
    private final HttpMethod method;

    /**
     * Creates a new <code>HttpClientHttpResponse</code> with a specified request and method
     *
     * @param httpRequest the request for which this is a response
     * @param httpMethod the method which was used to perform the request
     */
    public HttpClientHttpResponse(HttpRequest httpRequest, HttpMethod httpMethod)
    {
        this.request = httpRequest;
        this.method = httpMethod;
    }

    /**
     * @return whether or not the retrieved data is cached locally, this class does not cache data, so this
     * implementation always returns false
     */
    public final boolean isCached()
    {
        return false;
    }

    /**
     * Discovers whether the <code>HttpRequest</code> succeeded or failed
     *
     * @return true if the request failed, false if it succeeded
     */
    public final boolean isFailed()
    {
        return method == null || method.getStatusCode() < 200 || method.getStatusCode() > 299;
    }

    /**
     * Discovers whether or not the resource originally requested was found
     *
     * @return true if the resource was not found, false if it was
     */
    public final boolean isNotFound()
    {
        return method.getStatusCode() == 404;
    }

    /**
     * Discovers whether or not permission was granted to access the resource originally requested
     *
     * @return true if the resource is inaccessible, false if it is accessible
     */
    public final boolean isNotPermitted()
    {
        return method.getStatusCode() == 403 || method.getStatusCode() == 401;
    }

    /**
     * Get the <code>InputStream</code> which contains the data of the requested resource. The <code>isFailed()</code>,
     * <code>isNotFound</code> and <code>isNotPermitted</code> methods should be checked before calling this method.
     *
     * @return the input stream which can be read to obtain the data requested
     * @throws IOException if the stream cannot be read
     */
    public final InputStream getResponseBody() throws IOException
    {
        if (request.getMaximumSize() > 0)
            return new SizeLimitedInputStream(method.getResponseBodyAsStream(), request.getMaximumSize());
        else
            return method.getResponseBodyAsStream();
    }

    /**
     * @return the content type of the response, as described in its header
     */
    public final String getContentType()
    {
        return method.getResponseHeader("Content-type").toString();
    }

    /**
     * @return the status of the request as a text message
     */
    public final String getStatusMessage()
    {
        return method.getStatusText();
    }

    /**
     * @return the status of the request as a numerical code
     */
    public final int getStatusCode()
    {
        return method.getStatusCode();
    }

    /**
     * This method cleans up the response. It is strongly recommended that this method be called on a request once it is
     * no longer needed.
     */
    public final void finish()
    {
        method.releaseConnection();
    }

    public String getResponseBodyAsString() throws IOException
    {
        return method.getResponseBodyAsString();
    }

    public boolean isSuccessful()
    {
        return method.getStatusCode()==HttpServletResponse.SC_OK;
    }
    
    @Override
    public String toString()
    {
    	String response = null;
		try
		{
			response = getResponseBodyAsString();
		} catch (IOException e)
		{
			// ignore it, it's for debugging only
		}
		return "Status code: " + getStatusCode() + ", ResponseBody: " + response;
    }
}
