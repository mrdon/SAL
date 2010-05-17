package com.atlassian.sal.testresources.net;

import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;

import java.io.InputStream;
import java.util.Map;
import java.util.Collections;

/**
 * Mock response that provides setters for all properties
 */
public class MockResponse implements Response
{
    private int statusCode;
    private String responseBodyAsString;
    private InputStream responseBodyAsStream;
    private String statusText;
    private boolean successful;
    private Map<String, String> headers = Collections.emptyMap();

    public String getHeader(String name)
    {
        return headers.get(name);
    }

    public int getStatusCode()
    {
        return statusCode;
    }

    public void setStatusCode(int statusCode)
    {
        this.statusCode = statusCode;
    }

    public String getResponseBodyAsString()
    {
        return responseBodyAsString;
    }

    public void setResponseBodyAsString(String responseBodyAsString)
    {
        this.responseBodyAsString = responseBodyAsString;
    }

    public InputStream getResponseBodyAsStream()
    {
        return responseBodyAsStream;
    }

    public void setResponseBodyAsStream(InputStream responseBodyAsStream)
    {
        this.responseBodyAsStream = responseBodyAsStream;
    }

    public <T> T getEntity(Class<T> entityClass) throws ResponseException
    {
        throw new UnsupportedOperationException();
    }

    public String getStatusText()
    {
        return statusText;
    }

    public void setStatusText(String statusText)
    {
        this.statusText = statusText;
    }

    public boolean isSuccessful()
    {
        return successful;
    }

    public void setSuccessful(boolean successful)
    {
        this.successful = successful;
    }

    public Map<String, String> getHeaders()
    {
        return headers;
    }

    public void setHeaders(Map<String, String> headers)
    {
        this.headers = headers;
    }
}
