package com.atlassian.sal.ctk.test;

import com.atlassian.sal.ctk.CtkTest;
import com.atlassian.sal.ctk.CtkTestResults;
import com.atlassian.sal.api.component.ComponentLocator;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.net.*;
import com.atlassian.plugin.PluginManager;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class RequestFactoryTest implements CtkTest
{
    private final RequestFactory requestFactory;

    public RequestFactoryTest(RequestFactory requestFactory) {this.requestFactory = requestFactory;}
    private boolean passed = false;

    public void execute(CtkTestResults results) throws ResponseException
    {
        results.assertTrue("RequestFactory must be injectable", requestFactory != null);

        Request request = requestFactory.createRequest(Request.MethodType.GET, "http://google.com");

        request.execute(new ResponseHandler()
        {
            public void handle(Response response) throws ResponseException
            {
                passed = response.getResponseBodyAsString().contains("Google");
            }
        });
        results.assertTrue("Should be able to call http://google.com and get result that contains 'google'", passed);

        request = requestFactory.createRequest(Request.MethodType.GET, "http://demo.jira.com");
        request.addSeraphAuthentication("admin", "admin");

        request.execute(new ResponseHandler()
        {
            public void handle(Response response) throws ResponseException
            {
                passed = response.getResponseBodyAsString().contains("Joe Administrator");
            }
        });
        results.assertTrue("Should be able to call http://demo.jira.com and log in using seraph authentication", passed);
    }
}