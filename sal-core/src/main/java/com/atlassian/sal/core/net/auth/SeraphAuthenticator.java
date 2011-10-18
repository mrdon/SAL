package com.atlassian.sal.core.net.auth;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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
        if (queryString != null && queryString.contains("os_username"))
        {
            // It looks like someone has already set the username manually...
            return;
        }
        if (queryString == null)
        {
            queryString = "";
        }
        else
        {
            queryString += "&";
        }

        queryString += "os_username=" + urlEncode(username) + "&os_password=" + urlEncode(password);

        method.setQueryString(queryString);
    }

    private static String urlEncode(String str)
    {
        try
        {
            return URLEncoder.encode(str, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            // UTF-8 is standard, this should never happen
            throw new RuntimeException("Funny JRE you have here, it doesn't support UTF-8");
        }
    }
}
