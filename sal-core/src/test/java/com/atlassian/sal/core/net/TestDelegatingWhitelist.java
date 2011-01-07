package com.atlassian.sal.core.net;

import java.net.URI;

import com.atlassian.sal.api.net.Whitelist;

import com.google.common.collect.ImmutableList;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestDelegatingWhitelist
{
    @Test
    public void uriIsRejectedWhenNoDelegatesArePresent()
    {
        Whitelist whitelist = new DelegatingWhitelist(ImmutableList.<Whitelist>of());
        assertFalse(whitelist.allow(URI.create("http://example.com")));
    }
    
    @Test
    public void uriIsRejectedWhenAllDelegatesRejectTheUri()
    {
        Whitelist whitelist = new DelegatingWhitelist(ImmutableList.of(alwaysReject(), alwaysReject(), alwaysReject()));
        assertFalse(whitelist.allow(URI.create("http://example.com")));
    }

    @Test
    public void uriIsAllowedWhenTheFirstDelegateAllowsTheUri()
    {
        Whitelist whitelist = new DelegatingWhitelist(ImmutableList.of(alwaysAllow(), alwaysReject(), alwaysReject()));
        assertTrue(whitelist.allow(URI.create("http://example.com")));
    }

    @Test
    public void uriIsAllowedWhenTheLastDelegateAllowsTheUri()
    {
        Whitelist whitelist = new DelegatingWhitelist(ImmutableList.of(alwaysReject(), alwaysReject(), alwaysAllow()));
        assertTrue(whitelist.allow(URI.create("http://example.com")));
    }

    @Test
    public void urisIsAllowedWhenTheMiddleDelegateAllowsTheUri()
    {
        Whitelist whitelist = new DelegatingWhitelist(ImmutableList.of(alwaysReject(), alwaysAllow(), alwaysReject()));
        assertTrue(whitelist.allow(URI.create("http://example.com")));
    }
    
    private Whitelist alwaysAllow()
    {
        return AlwaysAllowWhitelist.INSTANCE;
    }

    enum AlwaysAllowWhitelist implements Whitelist
    {
        INSTANCE;

        public boolean allow(URI uri)
        {
            return true;
        }
    }

    private Whitelist alwaysReject()
    {
        return AlwaysRejectWhitelist.INSTANCE;
    }
    
    enum AlwaysRejectWhitelist implements Whitelist
    {
        INSTANCE;

        public boolean allow(URI uri)
        {
            return false;
        }
    }
}
