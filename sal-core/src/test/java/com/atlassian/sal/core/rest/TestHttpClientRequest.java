package com.atlassian.sal.core.rest;

import com.atlassian.sal.api.net.Request.MethodType;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ResponseHandler;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.core.net.HttpClientRequest;
import com.atlassian.sal.core.net.auth.HttpClientAuthenticator;
import com.atlassian.sal.core.trusted.CertificateFactory;
import junit.framework.TestCase;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.easymock.classextension.EasyMock;
import org.easymock.classextension.IMocksControl;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.Mockito.mock;

public class TestHttpClientRequest extends TestCase
{

    public void testAuthentication() throws IOException, ResponseException
    {
        final IMocksControl httpClientMockControl = EasyMock.createNiceControl();
        final HttpClient mockHttpClient = httpClientMockControl.createMock(HttpClient.class);
        httpClientMockControl.replay();


        // counter how many time was authentication called
        final AtomicInteger authenticatorCounter = new AtomicInteger(0);

        // lets create new GET request to http://url
        HttpClientRequest request = new HttpClientRequest(mockHttpClient, MethodType.GET, "http://url",
                mock(CertificateFactory.class), mock(UserManager.class));

        // this is our authenticator
        final HttpClientAuthenticator authenticator = new HttpClientAuthenticator()
        {
            public void process(HttpClient httpClient, HttpMethod method)
            {
                assertEquals("It should use mockClient", httpClient, mockHttpClient);
                assertTrue("We asked it for GetMethod", method instanceof GetMethod);
                authenticatorCounter.addAndGet(1);
            }
        };

        // lets add 2 authenticator to the request
        request.addAuthentication(authenticator);
        request.addAuthentication(authenticator);

        // and we are ready to execute the request
        request.execute(EasyMock.createMock(ResponseHandler.class));

        // and assert that authenticators were used
        assertEquals("Two authenticator should be called.", 2, authenticatorCounter.intValue());
    }

    public void testMaxNumberOfRedirectionReached() throws IOException
    {
        // create mock GetMethod - it should redirect few times
        final IMocksControl mockControl = EasyMock.createNiceControl();
        final GetMethod mockGetMethod = mockControl.createMock(GetMethod.class);
        mockGetMethod.getResponseHeader("location");
        mockControl.andReturn(new Header("location", "http://someRedirectionUrl"));
        mockControl.times(HttpClientRequest.MAX_REDIRECTS);
        mockControl.replay();

        // create HttpClient that will return 301 Moved Permanently
        final IMocksControl httpClientMockControl = EasyMock.createNiceControl();
        final HttpClient httpClientMock = httpClientMockControl.createMock(HttpClient.class);
        httpClientMock.executeMethod(mockGetMethod);
        httpClientMockControl.andReturn(302);
        httpClientMockControl.times(HttpClientRequest.MAX_REDIRECTS);
        httpClientMockControl.replay();

        // create a request that will return mockGetMethod
        HttpClientRequest request = new HttpClientRequest(httpClientMock, MethodType.GET, "http://url",
                mock(CertificateFactory.class), mock(UserManager.class))
        {
            @Override
            protected HttpMethod makeMethod()
            {
                return mockGetMethod;
            }
        };

        // now use it
        try
        {
            request.execute(EasyMock.createMock(ResponseHandler.class));
            fail("Should throw IOException - maximum retries reached.");
        }
        catch (ResponseException e)
        {
            // expect Exception
        }

        // and assert results
        mockControl.verify();
    }

    public void testAddRequestParametersFails()
    {
        // Lets try to add parameters to GET method
        try
        {
            HttpClientRequest request = new HttpClientRequest(EasyMock.createMock(HttpClient.class), MethodType.GET, "http://url",
                    mock(CertificateFactory.class), mock(UserManager.class));
            request.addRequestParameters("doIt", "quickly!");
            fail("Should throw exception that only the POST method can have parameters.");
        }
        catch (UnsupportedOperationException e)
        {
            // expected
        }

        // Lets try to add parameters to PUT method
        try
        {
            HttpClientRequest request = new HttpClientRequest(EasyMock.createMock(HttpClient.class), MethodType.PUT, "http://url",
                    mock(CertificateFactory.class), mock(UserManager.class));
            request.addRequestParameters("Isaid", "doIt", "now");
            fail("Should throw exception that only the POST method can have parameters.");
        }
        catch (UnsupportedOperationException e)
        {
            // expected
        }

        // Lets try to add uneven amount of parameters to POST method
        try
        {
            HttpClientRequest request = new HttpClientRequest(EasyMock.createMock(HttpClient.class), MethodType.POST, "http://url",
                    mock(CertificateFactory.class), mock(UserManager.class));
            request.addRequestParameters("doIt", "quickly!", "now");
            fail("Should throw exception that You must enter an even number of arguments.");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }

    public void testAddRequestParameters() throws IOException, ResponseException
    {
        // create mock PostMethod - someone should call addParamater() on it
        final IMocksControl mockControl = EasyMock.createNiceControl();
        final PostMethod mockPostMethod = mockControl.createMock(PostMethod.class);
        mockPostMethod.setRequestHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
        mockPostMethod.addParameter("a", "b");
        mockPostMethod.addParameter("a", "b");
        mockPostMethod.addParameter("a", "c");
        mockPostMethod.addParameter("x", "y");
        mockControl.replay();


        final IMocksControl httpClientMockControl = EasyMock.createNiceControl();
        final HttpClient mockHttpClient = httpClientMockControl.createMock(HttpClient.class);
        httpClientMockControl.replay();


        // create a request that will return mockPostMethod
        HttpClientRequest request = new HttpClientRequest(mockHttpClient, MethodType.POST, "http://url",
                mock(CertificateFactory.class), mock(UserManager.class))
        {
            @Override
            protected HttpMethod makeMethod()
            {
                return mockPostMethod;
            }
        };

        // now use it
        request.addRequestParameters("a", "b", "a", "b", "a", "c", "x", "y");
        request.execute(EasyMock.createMock(ResponseHandler.class));

        // and assert results
        mockControl.verify();
    }
}
