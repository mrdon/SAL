package com.atlassian.sal.core.net;

import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.core.trusted.CertificateFactory;
import junit.framework.TestCase;
import org.mockito.Mockito;

public class TestHttpClientProxyConfig extends TestCase {

    private CertificateFactory certificateFactory = Mockito.mock(CertificateFactory.class);
    private UserManager userManager = Mockito.mock(UserManager.class);
    private HttpClientRequestFactory httpClientRequestFactory;

    @Override
    public void tearDown() {
        System.setProperty("http.proxyHost", "");
        System.setProperty("http.nonProxyHosts", "");
    }

    @Override
    public void setUp() {
        this.httpClientRequestFactory = new HttpClientRequestFactory(certificateFactory, userManager);
        System.setProperty("http.proxyHost", "localhost");
        System.setProperty("http.nonProxyHosts", "localhost|*.jira.com");
    }

    public void testConfigureProxyEmptyURL()
    {
        httpClientRequestFactory.createRequest(Request.MethodType.GET, "");
    }

    public void testConfigureProxyValidURL()
    {
        httpClientRequestFactory.createRequest(Request.MethodType.GET, "localhost");
    }

}
