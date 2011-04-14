package com.atlassian.sal.core.net;

import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ResponseHandler;
import com.atlassian.sal.api.net.ReturningResponseHandler;
import com.atlassian.sal.api.net.auth.Authenticator;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.core.net.auth.BaseAuthenticator;
import com.atlassian.sal.core.net.auth.HttpClientAuthenticator;
import com.atlassian.sal.core.net.auth.SeraphAuthenticator;
import com.atlassian.sal.core.net.auth.TrustedTokenAuthenticator;
import com.atlassian.sal.core.trusted.CertificateFactory;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.OptionsMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.TraceMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HttpClient implementation of Request interface
 */
public class HttpClientRequest implements Request<HttpClientRequest, HttpClientResponse>
{
    private static final Logger log = Logger.getLogger(HttpClientRequest.class);

    public static final int MAX_REDIRECTS = 3;

    private final Request.MethodType methodType;
    private String url;
    private final Map<String, List<String>> parameters = new HashMap<String, List<String>>();
    private final Map<String, List<String>> headers = new HashMap<String, List<String>>();
    private final List<HttpClientAuthenticator> authenticators = new ArrayList<HttpClientAuthenticator>();
    private final CertificateFactory certificateFactory;

    private final HttpClient httpClient;
    private final UserManager userManager;
    private String requestBody;
    private String requestContentType;
    private boolean followRedirects = true;

    public HttpClientRequest(final HttpClient httpClient, final MethodType methodType, final String url,
                             final CertificateFactory certificateFactory, final UserManager userManager)
    {
        this.httpClient = httpClient;
        this.methodType = methodType;
        this.url = url;
        this.certificateFactory = certificateFactory;
        this.userManager = userManager;
        if (isEntityEnclosingMethod())
        {
            followRedirects = false;
        }
    }

    public HttpClientRequest setUrl(final String url)
    {
        this.url = url;
        // Reconfigure the proxy setting for the new URL
        // as it may or may not need to go through the system proxy
        configureProxy();
        return this;
    }

    // ------------------------ authenticators -------------------------------------------

    public HttpClientRequest addAuthentication(final Authenticator authenticator)
    {
        if (authenticator instanceof HttpClientAuthenticator)
        {
            this.authenticators.add((HttpClientAuthenticator) authenticator);
        }
        else
        {
            log.warn("Authenticator '" + authenticator + "'is not instance of " + HttpClientAuthenticator.class.getName());
        }
        return this;
    }

    public HttpClientRequest addTrustedTokenAuthentication()
    {
        final TrustedTokenAuthenticator trustedTokenAuthenticator = new TrustedTokenAuthenticator(
                userManager.getRemoteUsername(), certificateFactory);

        this.authenticators.add(trustedTokenAuthenticator);
        return this;
    }

    public HttpClientRequest addTrustedTokenAuthentication(final String username)
    {
        final TrustedTokenAuthenticator trustedTokenAuthenticator = new TrustedTokenAuthenticator(username,
                certificateFactory);

        this.authenticators.add(trustedTokenAuthenticator);
        return this;
    }

    public HttpClientRequest addBasicAuthentication(final String username, final String password)
    {
        this.authenticators.add(new BaseAuthenticator(username, password));
        return this;
    }

    public HttpClientRequest addSeraphAuthentication(final String username, final String password)
    {
        this.authenticators.add(new SeraphAuthenticator(username, password));
        return this;
    }

    // ------------------------ various setters -------------------------------------------

    public HttpClientRequest setConnectionTimeout(final int connectionTimeout)
    {
        final HttpConnectionManagerParams params = httpClient.getHttpConnectionManager().getParams();
        params.setConnectionTimeout(connectionTimeout);
        return this;
    }

    public HttpClientRequest setSoTimeout(final int soTimeout)
    {
        final HttpConnectionManagerParams params = httpClient.getHttpConnectionManager().getParams();
        params.setSoTimeout(soTimeout);
        return this;
    }

    public HttpClientRequest setRequestBody(final String requestBody)
    {
        this.requestBody = requestBody;
        if (!isEntityEnclosingMethod())
        {
            throw new IllegalArgumentException("Only POST and PUT methods can have request body");
        }
        return this;
    }

    private boolean isEntityEnclosingMethod()
    {
        return (methodType == MethodType.POST || methodType == MethodType.PUT);
    }

    public HttpClientRequest setEntity(Object entity)
    {
        throw new UnsupportedOperationException("This SAL request does not support object marshaling. Use the RequestFactory component instead.");
    }

    public HttpClientRequest setRequestContentType(final String requestContentType)
    {
        this.requestContentType = requestContentType;
        return this;
    }

    public HttpClientRequest addRequestParameters(final String... params)
    {
        if (methodType != MethodType.POST)
        {
            throw new UnsupportedOperationException("Only POST methods accept request parameters. For all other HTTP methods types http parameters have to be part of the URL string.");
        }

        if (params.length % 2 != 0)
        {
            throw new IllegalArgumentException("You must enter an even number of arguments");
        }

        for (int i = 0; i < params.length; i += 2)
        {
            final String name = params[i];
            final String value = params[i + 1];
            List<String> list = parameters.get(name);
            if (list == null)
            {
                list = new ArrayList<String>();
                parameters.put(name, list);
            }
            list.add(value);
        }
        return this;
    }

    public HttpClientRequest addHeader(final String headerName, final String headerValue)
    {
        List<String> list = headers.get(headerName);
        if (list == null)
        {
            list = new ArrayList<String>();
            headers.put(headerName, list);
        }
        list.add(headerValue);
        return this;
    }

    public HttpClientRequest setHeader(final String headerName, final String headerValue)
    {
        headers.put(headerName, new ArrayList<String>(Arrays.asList(headerValue)));
        return this;
    }
    
    public HttpClientRequest setFollowRedirects(boolean follow)
    {
        if (isEntityEnclosingMethod() && follow)
        {
            throw new IllegalStateException("Entity enclosing requests cannot be redirected without user intervention!");
        }
        this.followRedirects = follow;
        return this;
    }
    
    public HttpClientRequest addHeaders(final String... params)
    {
        if (params.length % 2 != 0)
        {
            throw new IllegalArgumentException("You must enter even number of arguments");
        }

        for (int i = 0; i < params.length; i += 2)
        {
            final String name = params[i];
            final String value = params[i + 1];
            List<String> list = headers.get(name);
            if (list == null)
            {
                list = new ArrayList<String>();
                headers.put(name, list);
            }
            list.add(value);
        }
        return this;
    }

    public <E> E executeAndReturn(ReturningResponseHandler<HttpClientResponse, E> httpClientResponseResponseHandler)
            throws ResponseException
    {
        final HttpMethod method = makeMethod();
        method.setFollowRedirects(followRedirects);
        processHeaders(method);
        processAuthenticator(method);
        processParameters(method);
        if (log.isDebugEnabled())
        {
            final Header[] requestHeaders = method.getRequestHeaders();
            log.debug("Calling " + method.getName() + " " + this.url + " with headers " + (requestHeaders == null ? "none" : Arrays.asList(requestHeaders).toString()));
        }
        method.setRequestHeader("Connection", "close");
        try
        {
            executeMethod(method, 0);
            return httpClientResponseResponseHandler.handle(new HttpClientResponse(method));
        }
        catch (IOException ioe)
        {
            throw new ResponseException(ioe);
        }
        finally
        {
            exhaustResponseContents(method);
            method.releaseConnection();
            // see https://extranet.atlassian.com/display/~doflynn/2008/05/19/HttpClient+leaks+sockets+into+CLOSE_WAIT
            final HttpConnectionManager httpConnectionManager = httpClient.getHttpConnectionManager();
            if (httpConnectionManager != null)
            {
                httpConnectionManager.closeIdleConnections(0);
            }
        }
    }

    /* (non-Javadoc)
     * @see com.atlassian.sal.api.net.Request#execute()
     */
    public void execute(final ResponseHandler<HttpClientResponse> responseHandler)
            throws ResponseException
    {
        executeAndReturn(new ReturningResponseHandler<HttpClientResponse, Void>()
        {
            public Void handle(final HttpClientResponse response) throws ResponseException
            {
                responseHandler.handle(response);
                return null;
            }
        });
    }

    private static void exhaustResponseContents(final HttpMethod response)
    {
        InputStream body = null;
        try
        {
            body = response.getResponseBodyAsStream();
            if (body == null)
            {
                return;
            }
            final byte[] buf = new byte[512];
            @SuppressWarnings("unused")
            int bytesRead = 0;
            while ((bytesRead = body.read(buf)) != -1)
            {
                // Read everything the server has to say before closing
                // the stream, or the server would get a unexpected
                // "connection closed" error.
            }
        }
        catch (final IOException e)
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
     *
     * @param input A (possibly null) InputStream
     */
    public static void shutdownStream(final InputStream input)
    {
        if (null == input)
        {
            return;
        }

        try
        {
            input.close();
        }
        catch (final IOException ioe)
        {
            // Do nothing
        }
    }

    public String execute() throws ResponseException
    {
        return executeAndReturn(new ReturningResponseHandler<HttpClientResponse, String>()
        {
            public String handle(final HttpClientResponse response) throws ResponseException
            {
                if (!response.isSuccessful())
                {
                    throw new ResponseException("Unexpected response received. Status code: " + response.getStatusCode());
                }
                return response.getResponseBodyAsString();
            }
        });
    }
    // ------------------------------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------- private methods ------------------------------------------------------------------
    // ------------------------------------------------------------------------------------------------------------------------------------

    protected HttpMethod makeMethod()
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

    /**
     * Configures the proxy for the underlying HttpClient.
     *
     */
    protected void configureProxy()
    {
        new HttpClientProxyConfig().configureProxy(this.httpClient, this.url);
    }

    private void executeMethod(final HttpMethod method, int redirectCounter) throws IOException
    {
        if (++redirectCounter > MAX_REDIRECTS)
        {
            throw new IOException("Maximum number of redirects (" + MAX_REDIRECTS + ") reached.");
        }
        else
        {
            // execute the method.
            final int statusCode = httpClient.executeMethod(method);

            if (followRedirects && statusCode >= 300 && statusCode <= 399)
            {
                String redirectLocation;
                final Header locationHeader = method.getResponseHeader("location");
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
        }
    }

    private void processHeaders(final HttpMethod method)
    {
        for (final String headerName : this.headers.keySet())
        {
            for (final String headerValue : this.headers.get(headerName))
            {
                method.addRequestHeader(headerName, headerValue);
            }
        }
    }

    private void processParameters(final HttpMethod method)
    {
        if (!(method instanceof EntityEnclosingMethod))
        {
            return;    // only POST and PUT method can apply
        }
        // Add post parameters
        if ((method instanceof PostMethod) && !this.parameters.isEmpty())
        {
            final PostMethod postMethod = (PostMethod) method;
            postMethod.setRequestHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
            for (final String parameterName : this.parameters.keySet())
            {
                for (final String parameterValue : this.parameters.get(parameterName))
                {
                    postMethod.addParameter(parameterName, parameterValue);
                }
            }
            return;
        }

        // Set request body
        if (this.requestBody != null)
        {
            final EntityEnclosingMethod entityEnclosingMethod = (EntityEnclosingMethod) method;
            final String contentType = requestContentType + "; charset=UTF-8";
            ByteArrayInputStream inputStream;
            try
            {
                inputStream = new ByteArrayInputStream(requestBody.getBytes("UTF-8"));
            }
            catch (final UnsupportedEncodingException e)
            {
                throw new RuntimeException(e);
            }
            entityEnclosingMethod.setRequestEntity(new InputStreamRequestEntity(inputStream, contentType));

        }
    }

    private void processAuthenticator(final HttpMethod method)
    {
        for (final HttpClientAuthenticator authenticator : authenticators)
        {
            authenticator.process(httpClient, method);
        }
    }

    public Map<String, List<String>> getHeaders()
    {
        return Collections.unmodifiableMap(headers);
    }

    public MethodType getMethodType()
    {
        return methodType;
    }

    @Override
    public String toString()
    {
        return methodType + " " + url + ", Parameters: " + parameters +
                (StringUtils.isBlank(requestBody) ? "" : "\nRequest body:\n" + requestBody);
    }

    
}
