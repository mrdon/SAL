package com.atlassian.sal.core.rest;

import com.atlassian.sal.api.net.Request.MethodType;
import com.atlassian.sal.api.net.ResponseConnectTimeoutException;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ResponseHandler;
import com.atlassian.sal.api.net.ResponseProtocolException;
import com.atlassian.sal.api.net.ResponseReadTimeoutException;
import com.atlassian.sal.api.net.ResponseStatusException;
import com.atlassian.sal.api.net.ResponseTransportException;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.core.net.HttpClientRequest;
import com.atlassian.sal.core.net.HttpClientResponse;
import com.atlassian.sal.core.net.auth.HttpClientAuthenticator;
import com.atlassian.sal.core.trusted.CertificateFactory;
import junit.framework.TestCase;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NoHttpResponseException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.easymock.classextension.EasyMock;
import org.easymock.classextension.IMocksControl;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.easymock.EasyMock.isA;
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

    public void testConnectTimeout() throws IOException, ResponseException
    {
        // create HttpClient that will throw a ConnectTimeoutException
        final IMocksControl httpClientMockControl = EasyMock.createNiceControl();
        final HttpClient httpClientMock = httpClientMockControl.createMock(HttpClient.class);
        httpClientMock.executeMethod(isA(GetMethod.class));
        httpClientMockControl.andThrow(new ConnectTimeoutException("errordescription"));
        httpClientMockControl.replay();

        HttpClientRequest request = createMockRequest(httpClientMock, MethodType.GET, "http://url");

        try
        {
            request.execute(EasyMock.createMock(ResponseHandler.class));
            fail("Should throw ResponseConnectTimeoutException");
        }
        catch (ResponseConnectTimeoutException e)
        {
            // expect Exception
            assertEquals(ConnectTimeoutException.class, e.getCause().getClass());
            assertEquals("errordescription", e.getMessage());
        }
    }

    public void testReadTimeout() throws IOException, ResponseException
    {
        // create HttpClient that will throw a NoHttpResponseException
        final IMocksControl httpClientMockControl = EasyMock.createNiceControl();
        final HttpClient httpClientMock = httpClientMockControl.createMock(HttpClient.class);
        httpClientMock.executeMethod(isA(GetMethod.class));
        httpClientMockControl.andThrow(new NoHttpResponseException("errordescription"));
        httpClientMockControl.replay();

        HttpClientRequest request = createMockRequest(httpClientMock, MethodType.GET, "http://url");

        try
        {
            request.execute(EasyMock.createMock(ResponseHandler.class));
            fail("Should throw ResponseConnectTimeoutException");
        }
        catch (ResponseReadTimeoutException e)
        {
            // expect Exception
            assertEquals(NoHttpResponseException.class, e.getCause().getClass());
            assertEquals("errordescription", e.getMessage());
        }
    }

    public void testHttpException() throws IOException, ResponseException
    {
        // create HttpClient that will throw an HttpException
        final IMocksControl httpClientMockControl = EasyMock.createNiceControl();
        final HttpClient httpClientMock = httpClientMockControl.createMock(HttpClient.class);
        httpClientMock.executeMethod(isA(GetMethod.class));
        httpClientMockControl.andThrow(new HttpException("errordescription"));
        httpClientMockControl.replay();

        HttpClientRequest request = createMockRequest(httpClientMock, MethodType.GET, "http://url");

        try
        {
            request.execute(EasyMock.createMock(ResponseHandler.class));
            fail("Should throw ResponseProtocolException");
        }
        catch (ResponseProtocolException e)
        {
            // expect Exception
            assertEquals(HttpException.class, e.getCause().getClass());
            assertEquals("errordescription", e.getMessage());
        }
    }

    public void testSocketException() throws IOException, ResponseException
    {
        // create HttpClient that will throw a SocketException
        final IMocksControl httpClientMockControl = EasyMock.createNiceControl();
        final HttpClient httpClientMock = httpClientMockControl.createMock(HttpClient.class);
        httpClientMock.executeMethod(isA(GetMethod.class));
        httpClientMockControl.andThrow(new SocketException("errordescription"));
        httpClientMockControl.replay();

        HttpClientRequest request = createMockRequest(httpClientMock, MethodType.GET, "http://url");

        try
        {
            request.execute(EasyMock.createMock(ResponseHandler.class));
            fail("Should throw ResponseTransportException");
        }
        catch (ResponseTransportException e)
        {
            // expect Exception
            assertEquals(SocketException.class, e.getCause().getClass());
            assertEquals("errordescription", e.getMessage());
        }
    }

    public void testStatusException() throws IOException, ResponseException
    {
        // create mock GetMethod that returns a 400 error
        final IMocksControl mockControl = EasyMock.createNiceControl();
        final GetMethod mockGetMethod = mockControl.createMock(GetMethod.class);
        mockGetMethod.getStatusCode();
        mockControl.andReturn(400);
        mockControl.anyTimes();
        mockControl.replay();

        // create HttpClient that will return this response
        final IMocksControl httpClientMockControl = EasyMock.createNiceControl();
        final HttpClient httpClientMock = httpClientMockControl.createMock(HttpClient.class);
        httpClientMock.executeMethod(mockGetMethod);
        httpClientMockControl.andReturn(400);
        httpClientMockControl.replay();

        HttpClientRequest request = createMockRequest(httpClientMock, mockGetMethod, MethodType.GET, "http://url");

        try
        {
            request.execute();
            fail("Should throw ResponseStatusException");
        }
        catch (ResponseStatusException e)
        {
            // expect Exception
            assertNotNull(e.getResponse());
            assertEquals(400, e.getResponse().getStatusCode());
        }
    }
    
    public void testMaxNumberOfRedirectionReached() throws IOException, ResponseException
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

        HttpClientRequest request = createMockRequest(httpClientMock, mockGetMethod, MethodType.GET, "http://url");

        // now use it
        try
        {
            request.execute(EasyMock.createMock(ResponseHandler.class));
            fail("Should throw ResponseProtocolException - maximum retries reached.");
        }
        catch (ResponseProtocolException e)
        {
            // expect Exception
        }

        // and assert results
        mockControl.verify();
    }
    
    public void testNoFollowRedirect() throws IOException
    {
        // create mock GetMethod - it should not redirect automatically
        final IMocksControl mockControl = EasyMock.createNiceControl();
        final GetMethod mockGetMethod = mockControl.createMock(GetMethod.class);
        mockGetMethod.getResponseHeader("location");
        mockControl.andReturn(new Header("location", "http://someRedirectionUrl")).anyTimes();
        mockControl.replay();

        // create HttpClient that will return 301 Moved Permanently
        final IMocksControl httpClientMockControl = EasyMock.createNiceControl();
        final HttpClient httpClientMock = httpClientMockControl.createMock(HttpClient.class);
        httpClientMock.executeMethod(mockGetMethod);
        httpClientMockControl.andReturn(302).anyTimes();
        httpClientMockControl.replay();

        HttpClientRequest request = createMockRequest(httpClientMock, mockGetMethod, MethodType.GET, "http://url");
        request.setFollowRedirects(false);
               
        // now use it
        try
        {
            request.execute(EasyMock.createMock(ResponseHandler.class));
            
        }
        catch (ResponseException e)
        {
            fail(e.getMessage());
        }

        // and assert results
        mockControl.verify();
    }

    public void testFollowRedirectForPostMethodsNotPossible() throws Exception
    {
        // create a request that will return mockGetMethod
        HttpClientRequest request = createMockRequest(MethodType.POST, "http://url");
        try
        {
            request.setFollowRedirects(true);
            fail("Should have thrown an exception because we can't follow redirects for Post methods");
        } catch (IllegalStateException ex)
        {
            //Expected
            assertEquals("Entity enclosing requests cannot be redirected without user intervention!", ex.getMessage());
        }
    }


    public void testFollowRedirectForPutMethodsNotPossible() throws Exception
    {
        HttpClientRequest request = createMockRequest(MethodType.PUT, "http://url");

        try
        {
            request.setFollowRedirects(true);
            fail("Should have thrown an exception because we can't follow redirects for Post methods");
        } catch (IllegalStateException ex)
        {
            //Expected
            assertEquals("Entity enclosing requests cannot be redirected without user intervention!", ex.getMessage());
        }
    }

    public void testExecutePostMethodNoFollowRedirects() throws Exception
    {
        final HttpClient httpClientMock = mock(HttpClient.class);
        final PostMethod postMethod = mock(PostMethod.class);

        HttpClientRequest request = createMockRequest(httpClientMock, postMethod, MethodType.POST, "http://url");

        request.execute(new ResponseHandler<HttpClientResponse>(){
            public void handle(final HttpClientResponse response) throws ResponseException
            {

            }
        });
        Mockito.verify(postMethod).setFollowRedirects(false);
    }

    public void testExecutePutMethodNoFollowRedirects() throws Exception
    {
        final HttpClient httpClientMock = mock(HttpClient.class);
        final PutMethod putMethod = mock(PutMethod.class);

        HttpClientRequest request = createMockRequest(httpClientMock, putMethod, MethodType.PUT, "http://url");

        request.execute(new ResponseHandler<HttpClientResponse>(){
            public void handle(final HttpClientResponse response) throws ResponseException
            {

            }
        });
        Mockito.verify(putMethod).setFollowRedirects(false);
    }

    public void testAddRequestParametersFails()
    {
        // Lets try to add parameters to GET method
        try
        {
            HttpClientRequest request = createMockRequest(MethodType.GET, "http://url");
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
            HttpClientRequest request = createMockRequest(MethodType.PUT, "http://url");
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
            HttpClientRequest request = createMockRequest(MethodType.POST, "http://url");
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
        HttpClientRequest request = createMockRequest(mockHttpClient, mockPostMethod, MethodType.POST, "http://url");

        // now use it
        request.addRequestParameters("a", "b", "a", "b", "a", "c", "x", "y");
        request.execute(EasyMock.createMock(ResponseHandler.class));

        // and assert results
        mockControl.verify();
    }

    private HttpClientRequest createMockRequest(MethodType methodType, String url)
    {
        return createMockRequest(EasyMock.createMock(HttpClient.class), methodType, url);
    }

    private HttpClientRequest createMockRequest(HttpClient client, MethodType methodType, String url)
    {
        return new HttpClientRequest(client, methodType, url,
                                     mock(CertificateFactory.class), mock(UserManager.class));
    }

    private HttpClientRequest createMockRequest(HttpClient client, final HttpMethod method, MethodType methodType,
                                                String url)
    {
        return new HttpClientRequest(client, methodType, url, mock(CertificateFactory.class), mock(UserManager.class))
            {
                @Override
                protected HttpMethod makeMethod()
                {
                    return method;
                }
            };
    }
}
