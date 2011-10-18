package com.atlassian.sal.core.net;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.atlassian.sal.core.net.auth.SeraphAuthenticator;

@RunWith(MockitoJUnitRunner.class)
public class TestSeraphAuthenticator
{
    private HttpClient client;
    private @Mock HttpMethod method;
    
    @Before
    public void createClient()
    {
        client = new HttpClient();
    }
    
    @After
    public void destroyClient()
    {
        client = null;
    }
    
    @Test
    public void verifyThatQueryParamsAreSetWhenNoneExist()
    {
        SeraphAuthenticator auth = new SeraphAuthenticator("joe", "bob");
        auth.process(client, method);
        
        verify(method).setQueryString("os_username=joe&os_password=bob");
    }
    
    @Test
    public void verifyThatQueryParamsAreAppendedWhenSomeAlreadyExist()
    {
        SeraphAuthenticator auth = new SeraphAuthenticator("joe", "bob");
        when(method.getQueryString()).thenReturn("cool=beans");
        auth.process(client, method);
        
        verify(method).setQueryString("cool=beans&os_username=joe&os_password=bob");
    }
    
    @Test
    public void verifyThatQueryParamsAreNotSetWhenOsUsernameAlreadyPresent()
    {
        SeraphAuthenticator auth = new SeraphAuthenticator("joe", "bob");
        when(method.getQueryString()).thenReturn("os_username=joe");
        auth.process(client, method);
        
        verify(method, never()).setQueryString(anyString());
    }
    
    @Test
    public void verifyThatQueryParamsAreProperlyUrlEncoded() throws UnsupportedEncodingException
    {
        final String username = "joe bob";
        final String password = ";/?:@&=+$,-_.!~*'()%";
        
        SeraphAuthenticator auth = new SeraphAuthenticator(username, password);
        auth.process(client, method);
        
        verify(method).setQueryString("os_username=" + URLEncoder.encode(username, "UTF-8") +
                "&os_password=" + URLEncoder.encode(password, "UTF-8"));
    }
}
