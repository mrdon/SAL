package com.atlassian.sal.api.http.httpclient;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.methods.OptionsMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.TraceMethod;

import com.atlassian.sal.api.http.HttpRequest;

public class HttpClientHttpRequest extends HttpRequest
{

    public HttpMethod createMethod()
    {
        return createMethod(getUrl(), this);
    }
    /**
     * Creates an <code>HttpMethod</code> of type defined by the request's method type. This method allows the subclass
     * to create a method whose URL differs from that of the request, although typically this URL is obtained by
     * <code>request.getUrl()</code>,
     *
     * @param url the url to use
     * @param request the request object, used to determine the method type to retrieve
     * @return a method of the type supplied by the request, which can retrieve data from the specified URL
     * @throws IllegalArgumentException if the method type specified by <code>request</code> is null or not supported
     */
    public static HttpMethod createMethod(String url, HttpRequest request) throws IllegalArgumentException
    {
        return createMethod(request.getMethodType(), url);
    }
    
    
    /**
     * Creates an <code>HttpMethod</code> of type defined by the request's method type. This method allows the subclass
     * to create a method whose URL differs from that of the request, although typically this URL is obtained by
     * <code>request.getUrl()</code>,
     *
     * @param url the url to use
     * @return a method of the type supplied by the request, which can retrieve data from the specified URL
     * @throws IllegalArgumentException 
     */
    public static HttpMethod createMethod(HttpMethodType methodType, String url)
    {
        HttpMethod method = makeRawMethod(methodType, url);
        method.setRequestHeader("Connection", "close");
        return method;
    }

    private static HttpMethod makeRawMethod(HttpMethodType methodType, String url)
    {
        switch(methodType)
        {
            case DELETE:
                return new DeleteMethod(url);
            case GET:
                return new GetMethod(url);
            case POST:
                return new PostMethod(url);
            case PUT:
                return new PutMethod(url);
            case HEAD:
                return new HeadMethod(url);
            case OPTIONS:
                return new OptionsMethod(url);
            case TRACE:
                return new TraceMethod(url);
            default:
                throw new IllegalArgumentException("Unknown method type: " + methodType);
        }
    }

}
