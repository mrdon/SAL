package com.atlassian.sal.api.net.auth;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;


public interface HttpClientAuthenticator extends Authenticator
{
	public void process(HttpClient httpClient, HttpMethod method);
}
