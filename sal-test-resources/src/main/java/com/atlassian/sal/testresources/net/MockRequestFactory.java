package com.atlassian.sal.testresources.net;

import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.net.Request;

import java.util.Map;
import java.util.HashMap;

/**
 * Mock request factory.  Either mock a request and call addRequest() with the URL you want that request
 * returned for, or the factory, if no request has been set for that URL, will create a MockRequest for you.
 */
public class MockRequestFactory implements RequestFactory<Request<?>>
{
    private final Map<String, Request<?>> requestMap = new HashMap<String, Request<?>>();

    public void addRequest(String url, Request<?> request)
    {
        requestMap.put(url, request);
    }

    public Request<?> createRequest(Request.MethodType methodType, String url)
    {
        if (requestMap.containsKey(url))
        {
            return requestMap.get(url);
        }
        else
        {
            return new MockRequest(methodType, url);
        }
    }
}
