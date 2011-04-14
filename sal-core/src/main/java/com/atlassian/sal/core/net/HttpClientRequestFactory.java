package com.atlassian.sal.core.net;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.log4j.Logger;

import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.net.Request.MethodType;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.core.trusted.CertificateFactory;

public class HttpClientRequestFactory implements RequestFactory<HttpClientRequest>
{
    private static final Logger log = Logger.getLogger(HttpClientRequestFactory.class);

    private final CertificateFactory certificateFactory;
    private final UserManager userManager;

    public HttpClientRequestFactory(final CertificateFactory certificateFactory, final UserManager userManager)
    {
        this.certificateFactory = certificateFactory;
        this.userManager = userManager;
    }

    /**
     * The default time to wait without retrieving data from the remote connection
     */
    public static final int DEFAULT_SOCKET_TIMEOUT=Integer.parseInt(System.getProperty("http.socketTimeout", "10000"));

    /**
     * The default time allowed for establishing a connection
     */
    public static final int DEFAULT_CONNECTION_TIMEOUT=Integer.parseInt(System.getProperty("http.connectionTimeout", "10000"));
    
    /* (non-Javadoc)
     * @see com.atlassian.sal.api.net.RequestFactory#createMethod(com.atlassian.sal.api.net.Request.MethodType, java.lang.String)
     */
    public HttpClientRequest createRequest(final MethodType methodType, final String url)
    {
        final HttpClient httpClient = getHttpClient(url);
        return new HttpClientRequest(httpClient, methodType, url, certificateFactory, userManager);
    }

    /**
     * @param url The URL
     * @return The HTTP client
     */
    protected HttpClient getHttpClient(final String url)
    {
        final HttpClient httpClient = new HttpClient();
        configureProxy(httpClient, url);
        configureConnectionParameters(httpClient);
        return httpClient;
    }

    /**
     * Applies a set of parameters to a client
     *
     * @param client the client to which parameters are applied
     * @param connectionParameters the parameters which will be applied
     */
    protected void configureConnectionParameters(final HttpClient httpClient)
    {
        final HttpConnectionManagerParams params = httpClient.getHttpConnectionManager().getParams();
        params.setSoTimeout(DEFAULT_SOCKET_TIMEOUT);
        params.setConnectionTimeout(DEFAULT_CONNECTION_TIMEOUT);
    }

    /**
     * @param client The client to configure the proxy of
     * @param remoteUrl The remote URL
     */
    protected void configureProxy(final HttpClient client, final String remoteUrl)
    {
        new HttpClientProxyConfig().configureProxy(client, remoteUrl);
    }
    
    public boolean supportsHeader()
    {
    	return true;
    }
}
