package com.atlassian.sal.core.net;

import java.net.URI;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.sal.api.net.Whitelist;

import com.google.common.collect.ImmutableList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestAppLinksWhitelist
{
    private static final URI REQUEST_URI = URI.create("http://example.com/some/path?key1=param1");

    @Mock ApplicationLinkService appLinkService;
    
    Whitelist whitelist;
    
    @Before
    public void createWhitelist()
    {
        whitelist = new AppLinksWhitelist(appLinkService);
    }
    
    @Test
    public void uriIsRejectedWhenThereAreNoAppLinks()
    {
        when(appLinkService.getApplicationLinks()).thenReturn(ImmutableList.<ApplicationLink>of());
        assertFalse(whitelist.allows(REQUEST_URI));
    }
    
    @Test
    public void uriIsRejectedWhenThereAreNoRpcUrlWhosePrefixMatchesRequestUri()
    {
        Iterable<ApplicationLink> appLinks = ImmutableList.of(newAppLink(URI.create("http://localhost:8800")));
        when(appLinkService.getApplicationLinks()).thenReturn(appLinks);
        assertFalse(whitelist.allows(REQUEST_URI));
    }
    
    @Test
    public void uriIsAllowedWhenThereIsAnRpcUrlWhosePrefixMatchesRequestUri()
    {
        Iterable<ApplicationLink> appLinks = ImmutableList.of(newAppLink(URI.create("http://example.com/")));
        when(appLinkService.getApplicationLinks()).thenReturn(appLinks);
        assertTrue(whitelist.allows(REQUEST_URI));
    }
    
    @Test
    public void uriIsAllowedWhenThereIsAnRpcUrlWhosePrefixMatchesRequestUriWithDiffereingCase()
    {
        Iterable<ApplicationLink> appLinks = ImmutableList.of(newAppLink(URI.create("http://EXAMPLE.COM/")));
        when(appLinkService.getApplicationLinks()).thenReturn(appLinks);
        assertTrue(whitelist.allows(REQUEST_URI));
    }
    
    private ApplicationLink newAppLink(URI rpcUri)
    {
        ApplicationLink appLink = mock(ApplicationLink.class);
        when(appLink.getRpcUrl()).thenReturn(rpcUri);
        return appLink;
    }
}
