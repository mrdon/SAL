package com.atlassian.sal.api.net;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpMethod;

public class HttpClientResponse implements Response
{

	private final HttpMethod method;

	public HttpClientResponse(HttpMethod method)
	{
		this.method = method;
	}

	public String getResponseBodyAsString() throws IOException
	{
		return method.getResponseBodyAsString();
	}

	public InputStream getResponseBodyAsStream() throws IOException
	{
		return method.getResponseBodyAsStream();
	}
	
	public int getStatusCode()
	{
		return method.getStatusCode();
	}

	public String getStatusText()
	{
		return method.getStatusText();
	}

	public boolean isSuccessful()
	{
        return method.getStatusCode()==HttpServletResponse.SC_OK;
	}

}
