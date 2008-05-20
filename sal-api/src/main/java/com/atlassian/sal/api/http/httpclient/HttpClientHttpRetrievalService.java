package com.atlassian.sal.api.http.httpclient;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Map;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;

import com.atlassian.sal.api.http.Authenticator;
import com.atlassian.sal.api.http.HttpParameters;
import com.atlassian.sal.api.http.HttpRequest;
import com.atlassian.sal.api.http.HttpResponse;
import com.atlassian.sal.api.http.HttpRetrievalService;

/**
 * Default retrieval service for JIRA Studio. <code>HttpClientRetrievalService</code> retrieves data from a specified
 * URL. Mechanisms exist for specifying timeouts and maximum amount of data retrieved.
 */
public final class HttpClientHttpRetrievalService implements HttpRetrievalService
{

    /**
     * A logger for use within this class
     */
    private static final Category log = Category.getInstance(HttpClientHttpRetrievalService.class);

    private static final int MAX_ATTEMPTS = 3;

    private static enum ExecuteResult { RETRY, COMPLETE }

    /**
     * Retrieves a resource from a URL using custom HttpRequest settings
     *
     * @param httpRequest the request we want to make
     * @throws IOException if the retrieval fails
     * @return a response object encapsulating the result of the request
     */
    public HttpResponse get(HttpRequest httpRequest) throws IOException
    {
        HttpParameters httpParameters = httpRequest.getHttpParameters();

        if(!httpParameters.isEnabled())
            throw new IOException("External connections have been disabled");

        URL remoteUrl = new URL(httpRequest.getUrl());
        HttpClient client = new HttpClient();

        configureProxy(client, remoteUrl);
        setClientParameters(client, httpParameters);

        boolean retry = true;
        int attempt = 0;
        HttpClientHttpResponse response = null;

        // Must ensure that every path through this code either (a) succeeds, or (b) calls method.releaseConnection()
        // JST-433
        while (retry)
        {
            attempt++;

            HttpMethod method = makeMethod(httpRequest);

            try
            {
                retry = (executeMethod(client, method, httpRequest, attempt) == ExecuteResult.RETRY);

                if (retry)
                    method.releaseConnection();
                else
                    response = new HttpClientHttpResponse(httpRequest, method);
            }
            catch (RuntimeException e)
            {
                method.releaseConnection();
                throw e;
            }
            catch (IOException e)
            {
                method.releaseConnection();
                log.info("Failed to download " + httpRequest.getUrl(), e);
                throw new IOException("Failed to download " + httpRequest.getUrl() + ": " + e.getMessage());
            }
        }

        return response;
    }

    private ExecuteResult executeMethod(HttpClient client, final HttpMethod method, HttpRequest httpRequest, int attempt) throws IOException
    {
        if (attempt > MAX_ATTEMPTS)
            throw new IOException("Maximum retries exceeded");

        if (httpRequest.getAuthenticator() != null)
            if(httpRequest.getAuthenticator() instanceof HttpClientAuthenticator)
                ((HttpClientAuthenticator) httpRequest.getAuthenticator()).preprocess(client, method);

        if (method instanceof PutMethod && httpRequest.getRequestBody() != null)
        {
            ((PutMethod)method).setRequestEntity(
                    new InputStreamRequestEntity(new ByteArrayInputStream(httpRequest.getRequestBody().getBytes("UTF-8")),
                    httpRequest.getRequestContentType() + "; charset=UTF-8"));
        }

        // execute the method.
        int statusCode = client.executeMethod(method);

        if (statusCode >= 300 && statusCode <= 399)
        {
            String redirectLocation;
            Header locationHeader = method.getResponseHeader("location");
            if (locationHeader != null)
            {
                redirectLocation = locationHeader.getValue();
                httpRequest.setUrl(redirectLocation);
                return ExecuteResult.RETRY;
            }
            else
            {
                // The response is invalid and did not provide the new location for
                // the resource.  Report an error or possibly handle the response
                // like a 404 Not Found error.
                throw new IOException("HTTP response returned redirect code " + statusCode + " but did not provide a location header");
            }
        }

        return ExecuteResult.COMPLETE;
    }

    private void configureProxy(HttpClient client, URL remoteUrl)
    {
        String proxyHost = System.getProperty("http.proxyHost");

        if (proxyHost != null && !isNonProxyHost(remoteUrl.getHost()))
        {
            int port = 80;
            try
            {
                port = Integer.parseInt(System.getProperty("http.proxyPort", "80"));
            }
            catch (NumberFormatException e)
            {
                log.warn("System property 'http.proxyPort' is not a number. Defaulting to 80.");
            }

            client.getHostConfiguration().setProxy(proxyHost, port);
            if(proxyAuthenticationRequired())
            {
                client.getState().setProxyCredentials(new AuthScope(proxyHost,port),
                        new UsernamePasswordCredentials(System.getProperty("http.proxyUser"),
                                System.getProperty("http.proxyPassword")));
            }
        }
    }

    /**
     * @return the version number to include in the client's user-agent string
     */
    private String getVersionNumber()
    {
        return "test version"; // todo: use the actual version, this is ticket HS-400
       // return GeneralUtil.getVersionNumber();
    }

    /**
     * Applies a set of parameters to a client
     *
     * @param client the client to which parameters are applied
     * @param connectionParameters the parameters which will be applied
     */
    private void setClientParameters(HttpClient client, HttpParameters connectionParameters)
    {
        HttpConnectionManagerParams params = client.getHttpConnectionManager().getParams();
        params.setSoTimeout(connectionParameters.getSocketTimeout());
        params.setConnectionTimeout(connectionParameters.getConnectionTimeout());
    }

    /**
     * Discover whether or not proxy authentication is required; if we are behind a proxy then it is required,
     * otherwise it isn't
     *
     * @return true if proxy authentication is required, false otherwise
     */
    private boolean proxyAuthenticationRequired()
    {
        return System.getProperty("http.proxyUser") != null;
    }

    private boolean isNonProxyHost(String host)
    {
        String httpNonProxyHosts = System.getProperty("http.nonProxyHosts");
        if (StringUtils.isBlank(httpNonProxyHosts))
        {
            // checking if property was misspelt, notice there is no 's' at the end of this property
            if (StringUtils.isBlank(System.getProperty("http.nonProxyHost")))
            {
                log.warn("The system property http.nonProxyHost is set. You probably meant to set http.nonProxyHosts.");
            }
            return false;
        }
        String[] nonProxyHosts = httpNonProxyHosts.split("\\|");
        for (String nonProxyHost : nonProxyHosts) {
            if (nonProxyHost.startsWith("*")) {
                if (host.endsWith(nonProxyHost.substring(1))) {
                    return true;
                }
            } else if (host.equals(nonProxyHost)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Attempts to make an <code>HttpMethod</code>, if the attempt fails, a default <code>GetMethod</code> is supplied.
     *
     * @param httpRequest contains the method type to make
     * @return an <code>HttpMethod</code> of type defined by <code>httpRequest.getType()</code>
     */
    private HttpMethod makeMethod(HttpRequest httpRequest)
    {
        Authenticator authenticator = httpRequest.getAuthenticator();

        HttpMethod method;
        if (authenticator != null && authenticator instanceof HttpClientAuthenticator)
        {
            method = ((HttpClientAuthenticator) authenticator).makeMethod(httpRequest);
        } else
        {
            method = HttpClientHttpRequest.createMethod(httpRequest.getUrl(), httpRequest);
        }

        if (method instanceof PostMethod)
        {
            method.setRequestHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
            for (Map.Entry<String, String> entry : httpRequest.getPostParams().entrySet())
            {
                ((PostMethod)method).addParameter(entry.getKey(), entry.getValue());
            }
        }

        // This belongs in the HttpClientHttpRequest?
        method.setRequestHeader("User-agent", MessageFormat.format(HTTP_USER_AGENT_STRING, getVersionNumber()));

        return method;
    }

    /**
     * Retrieves a resource from a URL using the default HttpRequest settings for that URL. The equivalent
     * of calling <code>get(service.getDefaultRequestFor(url));</code>
     *
     * @param url the URL to retrieve
     * @throws IOException if the retrieval fails
     * @return a response object encapsulating the result of the request
     */
    public HttpResponse get(String url) throws IOException
    {
        return get(getDefaultRequestFor(url));
    }

    /**
     * Retrieves the default configured HttpRequest for a particular URL. Will come prepackaged with
     * the configured system defaults for authentication, retrieval size and caching.
     *
     * @param url the URL that is going to be retrieved
     * @return the system default HttpRequest for that URL.
     */
    public HttpRequest getDefaultRequestFor(String url)
    {

        HttpRequest request = new HttpRequest();
        request.setMaximumCacheAgeInMillis(HttpRetrievalService.DEFAULT_MAX_DOWNLOAD_SIZE);
        request.setMaximumSize(HttpRetrievalService.DEFAULT_MAX_CACHE_AGE);
        request.setUrl(url);
        request.setAuthenticator(null);
        request.setHttpParameters(new HttpParameters(HttpParameters.DEFAULT_CONNECTION_TIMEOUT,
                                                                   HttpParameters.DEFAULT_SOCKET_TIMEOUT,
                                                                   true));
        return request;
    }
}
