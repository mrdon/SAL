package com.atlassian.sal.testresources.net;

import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ResponseHandler;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.auth.Authenticator;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

/**
 * Mock request that provides getters to all the information that is passed in, and also setters for the
 * response body that should be returned for execute(), or the response that should be passed to the
 * response handler.
 */
public class MockRequest implements Request<MockRequest>
{
    private final Request.MethodType methodType;
    private String url;
    private int connectionTimeout;
    private int soTimeout;
    private String requestBody;
    private String requestContentType;
    private List<String> requestParameters = new ArrayList<String>();
    private List<Authenticator> authenticators = new ArrayList<Authenticator>();
    private boolean trustedTokenAuthentication;
    private String trustedTokenUser;
    private String basicUser;
    private String basicPassword;
    private String seraphUser;
    private String seraphPassword;
    private Response response;
    private String responseBody;

    public MockRequest(MethodType methodType, String url)
    {
        this.methodType = methodType;
        this.url = url;
    }

    public MockRequest setConnectionTimeout(int connectionTimeout)
    {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    public MockRequest setSoTimeout(int soTimeout)
    {
        this.soTimeout = soTimeout;
        return this;
    }

    public MockRequest setUrl(String url)
    {
        this.url = url;
        return this;
    }

    public MockRequest setRequestBody(String requestBody)
    {
        this.requestBody = requestBody;
        return this;
    }

    public MockRequest setRequestContentType(String contentType)
    {
        this.requestContentType = contentType;
        return this;
    }

    public MockRequest addRequestParameters(String... params)
    {
        requestParameters.addAll(Arrays.asList(params));
        return this;
    }

    public MockRequest addAuthentication(Authenticator authenticator)
    {
        authenticators.add(authenticator);
        return this;
    }

    public MockRequest addTrustedTokenAuthentication()
    {
        trustedTokenAuthentication = true;
        return this;
    }

    public MockRequest addTrustedTokenAuthentication(String username)
    {
        trustedTokenAuthentication = true;
        trustedTokenUser = username;
        return this;
    }

    public MockRequest addBasicAuthentication(String username, String password)
    {
        basicUser = username;
        basicPassword = password;
        return this;
    }

    public MockRequest addSeraphAuthentication(String username, String password)
    {
        seraphUser = username;
        seraphPassword = password;
        return this;
    }

    public void execute(ResponseHandler responseHandler) throws ResponseException
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

    public void setResponse(Response response)
    {
        this.response = response;
    }

    public void setResponseBody(String responseBody)
    {
        this.responseBody = responseBody;
    }
}
