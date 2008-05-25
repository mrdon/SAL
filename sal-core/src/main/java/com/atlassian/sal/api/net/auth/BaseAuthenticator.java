package com.atlassian.sal.api.net.auth;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.log4j.Logger;

public class BaseAuthenticator implements HttpClientAuthenticator
{
	private static final Logger log = Logger.getLogger(BaseAuthenticator.class);
	private final String username;
	private final String password;
	
	public BaseAuthenticator(String username, String password)
	{
		this.username = username;
		this.password = password;
	}

	public void process(HttpClient httpClient, HttpMethod method)
	{
        try
        {
        	AuthScope authScope = new AuthScope(method.getURI().getHost(), AuthScope.ANY_PORT, null, AuthScope.ANY_SCHEME);
        	httpClient.getParams().setAuthenticationPreemptive(true);
			httpClient.getState().setCredentials(authScope, new UsernamePasswordCredentials(username, password));
        }
        catch (URIException e)
        {
            log.error("Unable to parse URI to set credentials: " + e, e);
        }
    }
}
