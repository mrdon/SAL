package com.atlassian.sal.core.net;

import java.net.URI;

import com.atlassian.sal.api.net.Whitelist;

import com.google.common.base.Predicate;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.any;

/**
 * A whitelist which delegates to 1 or more other whitelists and only allows access if one of the delegates allows
 * access. 
 */
public class DelegatingWhitelist implements Whitelist
{
    private final Iterable<Whitelist> whitelists;

    public DelegatingWhitelist(Iterable<Whitelist> whitelists)
    {
        this.whitelists = checkNotNull(whitelists, "whitelists");
    }
    
    public boolean allow(URI uri)
    {
        return any(whitelists, allows(checkNotNull(uri, "uri")));
    }

    private Predicate<Whitelist> allows(URI uri)
    {
        return new WhitelistAllows(uri);
    }
    
    private static final class WhitelistAllows implements Predicate<Whitelist>
    {
        private final URI uri;

        public WhitelistAllows(URI uri)
        {
            this.uri = uri;
        }

        public boolean apply(Whitelist whitelist)
        {
            return whitelist.allow(uri);
        }
    }
}
