package com.atlassian.sal.core.net;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.atlassian.sal.api.net.Request.MethodType;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.core.trusted.CertificateFactory;

public class HttpClientRequestFactory implements RequestFactory<HttpClientRequest>
{
    private static final Logger log = Logger.getLogger(HttpClientRequestFactory.class);

    private final CertificateFactory certificateFactory;

    public HttpClientRequestFactory(CertificateFactory certificateFactory)
    {
        this.certificateFactory = certificateFactory;
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
    public HttpClientRequest createRequest(MethodType methodType, String url)
    {
        final HttpClient httpClient = getHttpClient(url);
        return new HttpClientRequest(httpClient, methodType, url, certificateFactory);
    }

    /**
     * @param url The URL
     * @return The HTTP client
     */
    protected HttpClient getHttpClient(String url)
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
    protected void configureConnectionParameters(HttpClient httpClient)
    {
        final HttpConnectionManagerParams params = httpClient.getHttpConnectionManager().getParams();
        params.setSoTimeout(DEFAULT_SOCKET_TIMEOUT);
        params.setConnectionTimeout(DEFAULT_CONNECTION_TIMEOUT);
    }

    /**
     * @param client The client to configure the proxy of
     * @param remoteUrl The remote URL
     */
    protected void configureProxy(HttpClient client, String remoteUrl)
    {
        final String proxyHost = System.getProperty("http.proxyHost");

        URI uri;
        try
        {
            uri = new URI(remoteUrl);
        } catch (final URISyntaxException e)
        {
            log.warn("Invalid url: " + remoteUrl, e);
            return;
        }
        if (proxyHost != null && !isNonProxyHost(uri.getHost()))
        {
            int port = 80;
            try
            {
                port = Integer.parseInt(System.getProperty("http.proxyPort", "80"));
            }
            catch (final NumberFormatException e)
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
        final String httpNonProxyHosts = System.getProperty("http.nonProxyHosts");
        if (StringUtils.isBlank(httpNonProxyHosts))
        {
            // checking if property was misspelt, notice there is no 's' at the end of this property
            if (StringUtils.isBlank(System.getProperty("http.nonProxyHost")))
            {
                log.warn("The system property http.nonProxyHost is set. You probably meant to set http.nonProxyHosts.");
            }
            return false;
        }
        final String[] nonProxyHosts = httpNonProxyHosts.split("\\|");
        for (final String nonProxyHost : nonProxyHosts) {
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
}
