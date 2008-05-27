package com.atlassian.sal.api.rest;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.TestCase;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.easymock.classextension.EasyMock;
import org.easymock.classextension.IMocksControl;

import com.atlassian.sal.api.net.HttpClientRequest;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ResponseHandler;
import com.atlassian.sal.api.net.Request.MethodType;
import com.atlassian.sal.api.net.auth.HttpClientAuthenticator;

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
		HttpClientRequest request = new HttpClientRequest(mockHttpClient, MethodType.GET, "http://url");

		// this is our authenticator
		final HttpClientAuthenticator authenticator = new HttpClientAuthenticator()
		{
			public void process(HttpClient httpClient, HttpMethod method)
			{
				assertEquals("It should use mockClient", httpClient, mockHttpClient);
				assertTrue("We asked it for GetMethod",method instanceof GetMethod);
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
	
	public void testMaxAttemptsReached()
	{
		// counter how many time we tried to execute method
		final AtomicInteger executeCounter = new AtomicInteger(0);

		// lets have some mock client
		HttpClient mockHttpClient = new HttpClient()
		{
			@Override
			public int executeMethod(HttpMethod method) throws IOException, HttpException
			{
				executeCounter.addAndGet(1);
				throw new IOException("I am sick today, no connections please");
			}
		};
		
		// lets create new GET request to http://url
		HttpClientRequest request = new HttpClientRequest(mockHttpClient, MethodType.GET, "http://url");
		try
		{
			request.execute(EasyMock.createMock(ResponseHandler.class));
			fail("Should throw ResponseException - maximum retries reached.");
		} catch (ResponseException e)
		{
			assertEquals("It shoud try "+HttpClientRequest.MAX_ATTEMPTS+" times", HttpClientRequest.MAX_ATTEMPTS, executeCounter.get());
		}
	}

	public void testMaxNumberOfRedirectionReached() throws HttpException, IOException
	{
		// create mock GetMethod - it should redirect few times
		final IMocksControl mockControl = EasyMock.createNiceControl();
		final PostMethod mockPostMethod = mockControl.createMock(PostMethod.class);
		mockPostMethod.getResponseHeader("location");
		mockControl.andReturn(new Header("location", "http://someRedirectionUrl"));
		mockControl.times(HttpClientRequest.MAX_ATTEMPTS*HttpClientRequest.MAX_REDIRECTS);		// it should try to follow redirection MAX_REDIRECTS times and then retry MAX_ATTEMPTS times
		mockControl.replay();

		// create HttpClient that will return 301 Moved Permanently
		final IMocksControl httpClientMockControl = EasyMock.createNiceControl();
		final HttpClient httpClientMock = httpClientMockControl.createMock(HttpClient.class);
		httpClientMock.executeMethod(mockPostMethod);
		httpClientMockControl.andReturn(302);
		httpClientMockControl.times(HttpClientRequest.MAX_ATTEMPTS*HttpClientRequest.MAX_REDIRECTS);   // it should try to follow redirection MAX_REDIRECTS times and then retry MAX_ATTEMPTS times
		httpClientMockControl.replay();
		
		// create a request that will return mockPostMethod
		HttpClientRequest request = new HttpClientRequest(httpClientMock, MethodType.POST, "http://url")
		{
			@Override
			protected HttpMethod makeMethod()
			{
				return mockPostMethod;
			}
		};

		// now use it
		try
		{
			request.execute(EasyMock.createMock(ResponseHandler.class));
			fail("Should throw IOException - maximum retries reached.");
		} catch (ResponseException e)
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
			HttpClientRequest request = new HttpClientRequest(EasyMock.createMock(HttpClient.class), MethodType.GET, "http://url");
			request.addRequestParameters("doIt","quickly!");
			fail("Should throw exception that only POST and PUT methods can have parameters.");
		} catch (IllegalArgumentException e)
		{
			// expected
		}

		// Lets try to add uneven number of parameters
		try
		{
			HttpClientRequest request = new HttpClientRequest(EasyMock.createMock(HttpClient.class), MethodType.PUT, "http://url");
			request.addRequestParameters("Isaid", "doIt","now");
			fail("Should throw exception about uneven number of parameters.");
		} catch (IllegalArgumentException e)
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
		HttpClientRequest request = new HttpClientRequest(mockHttpClient, MethodType.POST, "http://url")
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
