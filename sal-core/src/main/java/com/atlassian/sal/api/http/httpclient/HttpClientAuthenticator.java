package com.atlassian.sal.api.http.httpclient;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;

import com.atlassian.sal.api.http.Authenticator;
import com.atlassian.sal.api.http.HttpRequest;

/**
 * Superclass for all <code>Authenticator</code> classes in this package. Provides default implementations for
 * <code>MakeMethod(HttpRequest)</code> and <code>preprocess(HttpClient, HttpMethod)</code>, as well as a utility method
 * for generating an <code>HttpMethod</code> of the appropriate type.
 */
abstract class HttpClientAuthenticator extends Authenticator
{

    /**
     * Creates an uncustomised <code>HttpMethod</code>. Subclasses should override this method to add subclass specific
     * information such as a modified URL or header information.
     *
     * @param request contains the data needed to
     * @return a method with URL and type supplied by <code>request</code>
     */
    public HttpMethod makeMethod(HttpRequest request)
    {
        return createMethod(request.getUrl(), request);
    }

    /**
     * Placeholder function for preprocessing of methods. This method does nothing so subclasses should override it
     * to perform preprocessing.
     *
     * @param client the client which will later be used to establish a connection
     * @param method the method to be preprocessed
     */
    public void preprocess(HttpClient client, HttpMethod method)
    {
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
    protected static HttpMethod createMethod(String url, HttpRequest request) throws IllegalArgumentException
    {
        return HttpClientHttpRequest.createMethod(request.getMethodType(), url);
    }
}
