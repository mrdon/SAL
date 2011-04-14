package com.atlassian.sal.core.net;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Configures the proxy on the HttpClient instance, reads the proxy configuration
 * from the following system properties:
 *
 *<ul>
 *     <li><b>http.proxyHost</b></li>
 *     <li><b>http.proxyPort</b> (optional, default to 80)</li>
 *</ul>
 * A list of excluded hosts can be provided to bypass the proxy:
 *<ul>
 *     <li><b>http.nonProxyHosts</b> (format: www.atlassian.com|*.example.com) - The only wildcard supported is the simple suffix match.</li>
 *</ul>
 * If the proxy needs authentication, the credentials must be provided using:
 *<ul>
 *     <li><b>http.proxyUser</b></li>
 *     <li><b>http.proxyPassword</b></li>
 *</ul>
 */
public class HttpClientProxyConfig {

    private static final Logger log = Logger.getLogger(HttpClientProxyConfig.class);

    /**
     * @param client The client to configure the proxy of
     * @param remoteUrl The remote URL
     */
    protected void configureProxy(final HttpClient client, final String remoteUrl)
    {
        if (client == null)
        {
            throw new IllegalArgumentException("Please provide a valid HttpClient instance");
        }
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

    private boolean isNonProxyHost(final String host)
    {
        if (StringUtils.isBlank(host))
        {
            return true;
        }
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
