package com.atlassian.sal.api.http;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * Stores the necessary values for an request made over the http protocol.
 */
public class HttpRequest
{
    public enum HttpMethodType
    {
        GET, POST, PUT, DELETE, HEAD, TRACE, OPTIONS;

    }

    /**
     * Default constructor; initialises the method type to GET_METHOD and everything else to null or zero.
     */
    public HttpRequest()
    {
        this.url = null;
        this.maximumCacheAgeInMillis = 0;
        this.maximumSize = 0;
        this.methodType = HttpMethodType.GET;
        this.httpParameters = null;
        this.authenticator = null;
        this.requestParams = new HashMap<String, String>();
    }

    /**
     * The URL of the request, typically starts with "http://"
     */
    private String url;


    /**
     * The maximum amount of data retrievable, in bytes. If an attempt is made to retrieve more data than this, then an
     * exception is thrown.
     */
    private int maximumSize;

    /**
     * The longest time that items will be held in the cache for, in milliseconds
     */
    private long maximumCacheAgeInMillis;

    /**
     * The authenticator which provides parameters to this request to allow the server to verify the request
     */
    private Authenticator authenticator;

    /**
     * The settings which define whether or not the request is enabled, and how long before the connection and sockets
     * will timeout
     */
    private HttpParameters httpParameters;

    /**
     * The method type of this request, must be chosen from <code>supportedMethods</code>
     */
    private HttpMethodType methodType;

    /**
     * The request parameters
     */
    private Map<String, String> requestParams;

    /**
     * The request body
     */
    private String requestBody;

    /**
     * The content type of the request body
     */
    private String requestContentType = "application/octet-stream";

    /**
     * Retrieves the target url
     *
     * @return the url to which this request will be targetted
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * Retrieve the maximum amount of data retrievable, in bytes.
     *
     * @return the maximum amount of data retrievable, in bytes. If an attempt is made to retrieve more data than this,
     * then an exception is thrown.
     */
    public int getMaximumSize()
    {
        return maximumSize;
    }

    /**
     *
     * @return the longest time an item may stay in the cache, in milliseconds
     */
    public long getMaximumCacheAgeInMillis()
    {
        return maximumCacheAgeInMillis;
    }

    /**
     * Retrieves the current <code>Authenticator</code>
     *
     * @return the <code>Authenticator</code> which takes care of madding user and password authentication to this
     * request
     */
    public Authenticator getAuthenticator()
    {
        return authenticator;
    }

    /**
     *
     * @return the information about whether this request is enabled, how long the connection timeout is, and how long
     * the socket timeout is
     */
    public HttpParameters getHttpParameters()
    {
        return httpParameters;
    }

    /**
     * Changes the target url. The new value is not checked for validity.
     *
     * @param url the new target
     */
    public void setUrl(String url)
    {
        this.url = url;
    }

    /**
     * Changes the maximum amount of data that can be retrieved by using this request
     *
     * @param maximumSize the new maximum number of bytes which can be retrieved using this request without causing an
     * exception
     */
    public void setMaximumSize(int maximumSize)
    {
        this.maximumSize = maximumSize;
    }

    /**
     * Changes the maximum time an item may stay in the cache
     *
     * @param maximumCacheAgeInMillis the new maximum time an item may stay in the cache, in milliseconds
     */
    public void setMaximumCacheAgeInMillis(long maximumCacheAgeInMillis)
    {
        this.maximumCacheAgeInMillis = maximumCacheAgeInMillis;
    }

    /**
     * Changes the <code>Authenticator</code>
     *
     * @param authenticator the new <code>Authenticator</code> which will take care of providing user and password
     * authentication for this request
     */
    public void setAuthenticator(Authenticator authenticator)
    {
        this.authenticator = authenticator;
    }

    /**
     * Changes the current timeout information held by this request.
     *
     * @param httpParameters the pararmeters specifying connection timeout, socket timeout and whether this
     * request is enabled at all
     */
    public void setHttpParameters(HttpParameters httpParameters)
    {
        this.httpParameters = httpParameters;
    }

    /**
     * Retrieves the type of this call.
     *
     * @return a string representing the type of this request (for example it may return "PUT_METHOD" to represent an
     * http put.
     */
    public HttpMethodType getMethodType()
    {
        return this.methodType;
    }

    public void setMethodType(HttpMethodType httpMethodType)
    {
        this.methodType = httpMethodType;
    }

    /**
     * Set the body of the request (for PUT or manually formatted POST requests)
     *
     * @param data a reader from which the body of the request can be retrieved
     */
    public void setRequestBody(final String data)
    {
        this.requestBody = data;
    }

    /**
     * Gets the body of the request
     *
     * @return the body of the request
     */
    public String getRequestBody()
    {
        return requestBody;
    }

    public void setRequestContentType(final String contentType)
    {
        this.requestContentType = contentType;
    }

    public String getRequestContentType()
    {
        return requestContentType;
    }

    /**
     * Add a parameter to be sent with a POST request (does not work with GET requests, you should
     * manipulate the query string directly)
     *
     * @param key the parameter to add
     * @param value the value of the parameter
     */
    public void addPostParameter(String key, String value)
    {
        if (!methodType.equals(HttpMethodType.POST))
            throw new IllegalStateException("Method must be POST to add post parameters. Method is: " + methodType);
        
        this.requestParams.put(key, value);
    }

    public Map<String, String> getPostParams()
    {
        return Collections.unmodifiableMap(requestParams);
    }
    
    @Override
    public String toString()
    {
    	return this.methodType + " " + this.url;
    }
}
