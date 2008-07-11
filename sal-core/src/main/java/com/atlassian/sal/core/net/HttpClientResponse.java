package com.atlassian.sal.core.net;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpMethod;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.Response;

public class HttpClientResponse implements Response
{

	private final HttpMethod method;

	public HttpClientResponse(HttpMethod method)
	{
		this.method = method;
	}

	public String getResponseBodyAsString() throws ResponseException
	{
		try
		{
			return method.getResponseBodyAsString();
		} catch (IOException e)
		{
			throw new ResponseException(e.getMessage(), e);
		}
	}

	public InputStream getResponseBodyAsStream() throws ResponseException
	{
		try
		{
			return method.getResponseBodyAsStream();
		} catch (IOException e)
		{
			throw new ResponseException(e.getMessage(), e);
		}
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
