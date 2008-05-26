package com.atlassian.sal.api.net;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.OptionsMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.TraceMethod;
import org.apache.log4j.Logger;

import com.atlassian.sal.api.component.ComponentLocator;
import com.atlassian.sal.api.net.auth.Authenticator;
import com.atlassian.sal.api.net.auth.BaseAuthenticator;
import com.atlassian.sal.api.net.auth.HttpClientAuthenticator;
import com.atlassian.sal.api.net.auth.SeraphAuthenticator;
import com.atlassian.sal.api.net.auth.TrustedTokenAuthenticator;
import com.atlassian.sal.api.user.UserManager;

/**
 *	HttpClient implementation of Request interface
 */
public class HttpClientRequest implements Request<HttpClientRequest>
{
	private static final Logger log = Logger.getLogger(HttpClientRequest.class);

	public static final int MAX_ATTEMPTS = 2;
	public static final int MAX_REDIRECTS = 3;
	
	private final MethodType methodType;
	private final String url;
	private final Map<String, List<String>> parameters = new HashMap<String, List<String>>();
	private final List<HttpClientAuthenticator> authenticators = new ArrayList<HttpClientAuthenticator>();

	private final HttpClient httpClient;
	private String requestBody;
	private String requestContentType;

	public HttpClientRequest(HttpClient httpClient, MethodType methodType, String url)
	{
		this.httpClient = httpClient;
		this.methodType = methodType;
		this.url = url;
	}
	// ------------------------ authenticators -------------------------------------------
	public HttpClientRequest addAuthentication(Authenticator authenticator)
	{
		if (authenticator instanceof HttpClientAuthenticator)
			this.authenticators.add((HttpClientAuthenticator) authenticator);
		else
			log.warn("Authenticator '"+authenticator+"'is not instance of " + HttpClientAuthenticator.class.getName());
		return this;
	}
	
	public HttpClientRequest addTrustedTokenAuthentication()
	{
		final UserManager userManager = ComponentLocator.getComponent(UserManager.class);
		final String remoteUsername = userManager.getRemoteUsername();
		if (remoteUsername != null && !remoteUsername.equals(""))
		{
			TrustedTokenAuthenticator trustedTokenAuthenticator = new TrustedTokenAuthenticator(remoteUsername);
			this.authenticators.add(trustedTokenAuthenticator);
		} 
		return this;
	}
	
	public HttpClientRequest addBasicAuthentication(String username, String password)
	{
		this.authenticators.add(new BaseAuthenticator(username, password));
		return this;
	}
	
	public HttpClientRequest addSeraphAuthentication(String username, String password)
	{
		this.authenticators.add(new SeraphAuthenticator(username, password));
		return this;
	}
	
	// ------------------------ various setters -------------------------------------------
	public HttpClientRequest setRequestBody(String requestBody)
	{
		this.requestBody = requestBody;
		if (methodType != MethodType.POST && methodType != MethodType.PUT)
		{
			throw new IllegalArgumentException("Only POST and PUT methods can have request body");
		} 
		return this;
	}
	
	public HttpClientRequest setRequestContentType(String requestContentType)
	{
		this.requestContentType = requestContentType;
		return this;
	}
	
	public HttpClientRequest addRequestParameters(String... params)
	{
		if (params.length%2!=0)
		{
			throw new IllegalArgumentException("You must enter even number of arguments");
		}
		
		if (methodType != MethodType.POST && methodType != MethodType.PUT)
		{
			throw new IllegalArgumentException("Only POST and PUT methods accept req");
		} 
		
		for (int i = 0; i < params.length; i+=2)
		{
			String name = params[i];
			String value = params[i+1];
			List<String> list = parameters.get(name);
			if (list==null)
			{
				list = new ArrayList<String>();
				parameters.put(name, list);
			}
			list.add(value);
		}
		return this;
	}
	
	/* (non-Javadoc)
	 * @see com.atlassian.sal.api.net.Request#execute()
	 */
	public void execute(ResponseHandler responseHandler) throws IOException
	{
		Throwable lastException = null;
		for (int attempts = 0; attempts < MAX_ATTEMPTS; attempts++)
		{
			HttpMethod method = makeMethod();
			processAuthenticator(method);
			processParameters(method);
			method.setRequestHeader("Connection", "close");
			try
			{
				executeMethod(method, 0);
				responseHandler.handle(new HttpClientResponse(method));
				return;	// success lets get out of here
			} catch (ExecuteMethodException e)
			{
				// this exception occurs during executeMethod(). keep retrying.
				lastException = e.getCause();
				log.debug(e,e);
			} catch (IOException e)
			{
				// this exception occurs during responseHandler.handle(). throw it all the way out.
				// this catch block is here only to improve readability of the code
				throw e;
			}
			finally
			{
				exhaustResponseContents(method);
				method.releaseConnection();
			}
		}
		// log exception if there was one
		if (lastException!=null)
		{
			log.warn(lastException,lastException);	// We probably shouldn't log and rethrow. But then we would lose stacktrace (new IOException doesn't have cause argument);
		}
		throw new IOException("Maximum number of retries ("+MAX_ATTEMPTS+") exceeded.");
	}

    private static void exhaustResponseContents(HttpMethod response)
    {
        InputStream body = null;
        try
        {
            body = response.getResponseBodyAsStream();
            if (body==null)
            {
            	return;
            }
            byte[] buf = new byte[512];
            @SuppressWarnings("unused")
			int bytesRead = 0;
            while ((bytesRead = body.read(buf)) != -1)
            {
                // throw the bytes away! :)
            }
        }
        catch (IOException e)
        {
            // Ignore, we're already done with the response anyway.
        }
        finally
        {
            shutdownStream(body);
        }
    }
    
    /**
     * Unconditionally close an <code>InputStream</code>.
     * Equivalent to {@link InputStream#close()}, except any exceptions will be ignored.
     * @param input A (possibly null) InputStream
     */
    public static void shutdownStream( final InputStream input )
    {
        if( null == input )
        {
            return;
        }

        try
        {
            input.close();
        }
        catch( final IOException ioe )
        {
        }
    }    
    

	public String execute() throws IOException
	{
		final Set<String> stringHolder = new HashSet<String>();
		execute(new ResponseHandler()
		{
			public void handle(Response response) throws IOException
			{
				if (!response.isSuccessful())
				{
					throw new IOException("Unexpected response received. Status code: " + response.getStatusCode());
				}
				stringHolder.add(response.getResponseBodyAsString());
			}
		});
		
		return stringHolder.isEmpty()?"":stringHolder.iterator().next();
	}
	// ------------------------------------------------------------------------------------------------------------------------------------
	// -------------------------------------------------- private methods ------------------------------------------------------------------
	// ------------------------------------------------------------------------------------------------------------------------------------
	
	private class ExecuteMethodException extends Exception
	{
		public ExecuteMethodException(Exception e)
		{
			super(e);
		}
	}
	
	protected HttpMethod makeMethod() throws UnsupportedEncodingException
	{
		final HttpMethod method;
		switch (methodType)
		{
		case POST:
			method = new PostMethod(url);
			break;
		case PUT:
			method = new PutMethod(url);
			break;
		case DELETE:
			method = new DeleteMethod(url);
			break;
		case OPTIONS:
			method = new OptionsMethod(url);
			break;
		case HEAD:
			method = new HeadMethod(url);
			break;
		case TRACE:
			method = new TraceMethod(url);
			break;
		default:
			method = new GetMethod(url);
		break;
		}
		return method;
	}
	
	private void executeMethod(final HttpMethod method, int redirectCounter) throws ExecuteMethodException
	{
		try
		{
			if (++redirectCounter>MAX_REDIRECTS)
				throw new IOException("Maximum number of redirects ("+MAX_REDIRECTS+") reached.");
			
			// execute the method.
			int statusCode = httpClient.executeMethod(method);
			
			if (statusCode >= 300 && statusCode <= 399)
			{
				String redirectLocation;
				Header locationHeader = method.getResponseHeader("location");
				if (locationHeader != null)
				{
					redirectLocation = locationHeader.getValue();
					method.setURI(new URI(redirectLocation, true));
					executeMethod(method, redirectCounter);
				}
				else
				{
					// The response is invalid and did not provide the new location for
					// the resource.  Report an error or possibly handle the response
					// like a 404 Not Found error.
					throw new IOException("HTTP response returned redirect code " + statusCode + " but did not provide a location header");
				}
			}
		} catch (URIException e)
		{
			throw new ExecuteMethodException(e);
		} catch (HttpException e)
		{
			throw new ExecuteMethodException(e);
		} catch (NullPointerException e)
		{
			throw new ExecuteMethodException(e);
		} catch (IOException e)
		{
			throw new ExecuteMethodException(e);
		}
	}
	
	private void processParameters(final HttpMethod method) throws UnsupportedEncodingException
	{
		if (!(method instanceof EntityEnclosingMethod))
		{
			return;	// only POST and PUT method can apply
		}
		// Add post parameters
		if ((method instanceof PostMethod) && !this.parameters.isEmpty())
		{
			PostMethod postMethod = (PostMethod) method;
			postMethod.setRequestHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
			for (String parameterName : this.parameters.keySet())
			{
				for (String parameterValue : this.parameters.get(parameterName))
				{
					postMethod.addParameter(parameterName, parameterValue);
				}
			}
			return;
		}
		
		// Set request body
		if ((method instanceof EntityEnclosingMethod) && this.requestBody!=null)
		{
			EntityEnclosingMethod entityEnclosingMethod = (EntityEnclosingMethod) method;
			final String contentType = requestContentType + "; charset=UTF-8";
			final ByteArrayInputStream inputStream = new ByteArrayInputStream(requestBody.getBytes("UTF-8"));
			entityEnclosingMethod.setRequestEntity(new InputStreamRequestEntity(inputStream, contentType));
			
		}
	}
	
	private void processAuthenticator(final HttpMethod method)
	{
		for (HttpClientAuthenticator authenticator : authenticators)
		{
			authenticator.process(httpClient, method);
		}
	}
}
