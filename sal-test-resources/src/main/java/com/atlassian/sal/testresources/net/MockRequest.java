package com.atlassian.sal.testresources.net;

import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ResponseHandler;
import com.atlassian.sal.api.net.auth.Authenticator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mock request that provides getters to all the information that is passed in, and also setters for the
 * response body that should be returned for execute(), or the response that should be passed to the
 * response handler.
 */
public class MockRequest implements Request<MockRequest, MockResponse>
{
    private final Request.MethodType methodType;
    private String url;
    private int connectionTimeout;
    private int soTimeout;
    private String requestBody;
    private String requestContentType;
    private final Map<String, List<String>> headers = new HashMap<String, List<String>>();
    private final List<String> requestParameters = new ArrayList<String>();
    private final List<Authenticator> authenticators = new ArrayList<Authenticator>();
    private boolean trustedTokenAuthentication;
    private String trustedTokenUser;
    private String basicUser;
    private String basicPassword;
    private String seraphUser;
    private String seraphPassword;
    private Response response;
    private String responseBody;

    public MockRequest(final MethodType methodType, final String url)
    {
        this.methodType = methodType;
        this.url = url;
    }

    public MockRequest setConnectionTimeout(final int connectionTimeout)
    {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    public MockRequest setSoTimeout(final int soTimeout)
    {
        this.soTimeout = soTimeout;
        return this;
    }

    public MockRequest setUrl(final String url)
    {
        this.url = url;
        return this;
    }

    public MockRequest setRequestBody(final String requestBody)
    {
        this.requestBody = requestBody;
        return this;
    }

    public MockRequest setRequestContentType(final String contentType)
    {
        this.requestContentType = contentType;
        return this;
    }

    public MockRequest addRequestParameters(final String... params)
    {
        requestParameters.addAll(Arrays.asList(params));
        return this;
    }

    public MockRequest addHeader(final String headerName, final String headerValue)
    {
        List<String> list = headers.get(headerName);
        if (list == null)
        {
            list = new ArrayList<String>();
            headers.put(headerName, list);
        }
        list.add(headerValue);
        return this;
    }

    public MockRequest setHeader(final String headerName, final String headerValue)
    {
        headers.put(headerName, new ArrayList<String>(Arrays.asList(headerValue)));
        return this;
    }

    public MockRequest addAuthentication(final Authenticator authenticator)
    {
        authenticators.add(authenticator);
        return this;
    }

    public MockRequest addTrustedTokenAuthentication()
    {
        trustedTokenAuthentication = true;
        return this;
    }

    public MockRequest addTrustedTokenAuthentication(final String username)
    {
        trustedTokenAuthentication = true;
        trustedTokenUser = username;
        return this;
    }

    public MockRequest addBasicAuthentication(final String username, final String password)
    {
        basicUser = username;
        basicPassword = password;
        return this;
    }

    public MockRequest addSeraphAuthentication(final String username, final String password)
    {
        seraphUser = username;
        seraphPassword = password;
        return this;
    }

    public void execute(final ResponseHandler responseHandler) throws ResponseException
    {
        if (response == null)
        {
            response = new MockResponse();
        }
        responseHandler.handle(response);
    }

    public String execute() throws ResponseException
    {
        return responseBody;
    }

    public MethodType getMethodType()
    {
        return methodType;
    }

    public String getUrl()
    {
        return url;
    }

    public int getConnectionTimeout()
    {
        return connectionTimeout;
    }

    public int getSoTimeout()
    {
        return soTimeout;
    }

    public String getRequestBody()
    {
        return requestBody;
    }

    public String getRequestContentType()
    {
        return requestContentType;
    }

    public List<String> getRequestParameters()
    {
        return requestParameters;
    }

    public Map<String, List<String>> getHeaders()
    {
        return headers;
    }

    public List<String> getHeader(final String headerName)
    {
        return headers.get(headerName);
    }

    public List<Authenticator> getAuthenticators()
    {
        return authenticators;
    }

    public boolean isTrustedTokenAuthentication()
    {
        return trustedTokenAuthentication;
    }

    public String getTrustedTokenUser()
    {
        return trustedTokenUser;
    }

    public String getBasicUser()
    {
        return basicUser;
    }

    public String getBasicPassword()
    {
        return basicPassword;
    }

    public String getSeraphUser()
    {
        return seraphUser;
    }

    public String getSeraphPassword()
    {
        return seraphPassword;
    }

    public Response getResponse()
    {
        return response;
    }

    public void setResponse(final Response response)
    {
        this.response = response;
    }

    public void setResponseBody(final String responseBody)
    {
        this.responseBody = responseBody;
    }
}
