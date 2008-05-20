package com.atlassian.sal.api.http.httpclient;

import com.atlassian.sal.api.http.HttpParameters;
import com.atlassian.sal.api.http.HttpRequest;
import com.atlassian.sal.api.http.HttpResponse;
import com.atlassian.sal.api.http.httpclient.HttpClientHttpRetrievalService;

import junit.framework.TestCase;

import java.io.IOException;

public class TestHttpClientHttpRetrievalService extends TestCase
{

    public void testHttpClientWithDisabledConnection()
    {
        HttpClientHttpRetrievalService service = new HttpClientHttpRetrievalService();

        HttpParameters params = new HttpParameters(HttpParameters.DEFAULT_CONNECTION_TIMEOUT, HttpParameters.DEFAULT_SOCKET_TIMEOUT, false);

        HttpRequest request = getDefaultRequest(params);
        try
        {
            service.get(request);
            fail("No exception thrown");
        }
        catch (IOException e)
        {
        }
    }

    public void testHttpClientWithVeryShortConnectionTimeout()
    {
        HttpParameters params = new HttpParameters(1, HttpParameters.DEFAULT_SOCKET_TIMEOUT, true);
        HttpRequest request = getDefaultRequest(params);

        HttpClientHttpRetrievalService service =  new HttpClientHttpRetrievalService();

        try
        {
            service.get(request);
            fail("No exception thrown");
        }
        catch (IOException e)
        {
        }
    }

    public void testHttpClientWithVeryShortTimeout()
    {
        HttpParameters params = new HttpParameters(HttpParameters.DEFAULT_CONNECTION_TIMEOUT, 1, true);
        HttpRequest request = getDefaultRequest(params);
        HttpClientHttpRetrievalService service = new HttpClientHttpRetrievalService();
        try
        {
            service.get(request);
            fail("No exception thrown");
        }
        catch (IOException e)
        {
        }
    }


    public void testHttpClientWithDefaultTimeout()
    {
        HttpParameters params = new HttpParameters(HttpParameters.DEFAULT_CONNECTION_TIMEOUT, HttpParameters.DEFAULT_SOCKET_TIMEOUT, true);
        HttpRequest request = getDefaultRequest(params);
        HttpClientHttpRetrievalService service = new HttpClientHttpRetrievalService();
        try
        {
            HttpResponse httpResponse = service.get(request);

            assertNotNull(httpResponse);
            assertNotNull(httpResponse.getResponseBody());
        }
        catch (IOException e)
        {
        }
    }

    public void testAccessInvalidURL()
    {
        HttpParameters params = new HttpParameters(HttpParameters.DEFAULT_CONNECTION_TIMEOUT, HttpParameters.DEFAULT_SOCKET_TIMEOUT, true);
        HttpRequest request = getDefaultRequest(params);
        request.setUrl("http://invalid.invalid");

        HttpClientHttpRetrievalService service = new HttpClientHttpRetrievalService();
        try
        {
            service.get(request);
            fail("No exception thrown");
        }
        catch (IOException e)
        {
        }

    }

    private HttpRequest getDefaultRequest(HttpParameters params)
    {
        HttpRequest request = new HttpRequest();
        request.setUrl("http://example.com");
        request.setHttpParameters(params);
        return request;
    }

}
