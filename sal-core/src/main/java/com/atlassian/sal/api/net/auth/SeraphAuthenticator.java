package com.atlassian.sal.api.net.auth;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;


public class SeraphAuthenticator implements HttpClientAuthenticator
{

	private final String username;
	private final String password;

	public SeraphAuthenticator(String username, String password)
	{
		this.username = username;
		this.password = password;
	}

	/**
	 * @param httpClient
	 * @param method
	 */
	public void process(HttpClient httpClient, HttpMethod method)
	{
		String queryString = method.getQueryString();
		if (queryString!=null && queryString.contains("os_username"))
		{
			// It looks like someone has already set the username manually...
			return;
		} 
		if (queryString==null)
		{
			queryString = "";
		}
		else 
		{
			queryString+="&";
		}
			
		queryString+="os_username="+username+"&os_password="+password;

		method.setQueryString(queryString);
	}

}
