package com.atlassian.sal.core.net;

import java.net.URI;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.sal.api.net.Whitelist;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Iterables.transform;

/**
 * Whitelist implementation which uses applinks as the whitelist source.
 */
public class AppLinksWhitelist implements Whitelist
{
    private final ApplicationLinkService appLinkService;

    public AppLinksWhitelist(ApplicationLinkService appLinkService)
    {
        this.appLinkService = checkNotNull(appLinkService, "appLinkService");
    }
    
    public boolean allow(URI uri)
    {
        return any(appLinks(), prefixes(checkNotNull(uri, "uri")));
    }

    private Iterable<URI> appLinks()
    {
        return transform(appLinkService.getApplicationLinks(), rpcUri());
    }

    private Function<ApplicationLink, URI> rpcUri()
    {
        return ExtractRpcUri.INSTANCE;
    }
    
    private enum ExtractRpcUri implements Function<ApplicationLink, URI>
    {
        INSTANCE;

        public URI apply(ApplicationLink appLink)
        {
            return appLink.getRpcUrl();
        }
    }

    private Predicate<URI> prefixes(URI uri)
    {
        return new UriPrefixPredicate(uri);
    }
    
    private static final class UriPrefixPredicate implements Predicate<URI>
    {
        private final String uri;

        public UriPrefixPredicate(URI uri)
        {
            this.uri = uri.normalize().toASCIIString().toLowerCase();
        }

        public boolean apply(URI prefix)
        {
            return uri.startsWith(prefix.normalize().toASCIIString().toLowerCase());
        }
    }
}
