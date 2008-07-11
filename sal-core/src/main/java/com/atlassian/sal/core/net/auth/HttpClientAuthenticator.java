package com.atlassian.sal.core.net.auth;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import com.atlassian.sal.api.net.auth.Authenticator;


public interface HttpClientAuthenticator extends Authenticator
{
	public void process(HttpClient httpClient, HttpMethod method);
}
