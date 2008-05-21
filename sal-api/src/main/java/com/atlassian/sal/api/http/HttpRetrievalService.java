package com.atlassian.sal.api.http;

import java.io.IOException;

/**
 * Interface for
 */
public interface HttpRetrievalService
{

    /**
     * The default maximum download size; treat anything above this size as suspect
     */
    public static final int DEFAULT_MAX_DOWNLOAD_SIZE = 500 * 1024 * 1024; // anything > 500k is suspect (malicious)

    /**
     * The default age for the cache; throw stuff out after this time
     */
    public static final int DEFAULT_MAX_CACHE_AGE = 30 * 60 * 1000; // cache for 30 minutes

    /**
     * Used to communicate product information
     */
    public static final String HTTP_USER_AGENT_STRING = "Atlassian/{0} (http://www.atlassian.com/)";

    /**
     * Retrieves a resource from a URL using the default HttpRequest settings for that URL. The equivalent
     * of calling <code>get(service.getDefaultRequestFor(url));</code>
     *
     * @param url the URL to retrieve
     * @throws IOException if the retrieval fails
     * @return a response object encapsulating the result of the request
     */
    public HttpResponse get(String url) throws IOException;

    /**
     * Retrieves a resource from a URL using custom HttpRequest settings
     *
     * @param httpRequest the request we want to make
     * @throws IOException if the retrieval fails
     * @return a response object encapsulating the result of the request
     */
    public HttpResponse get(HttpRequest httpRequest) throws IOException;

    /**
     * Retrieves the default configured HttpRequest for a particular URL. Will come prepackaged with
     * the configured system defaults for authentication, retrieval size and caching.
     *
     * @param url the URL that is going to be retrieved
     * @return the system default HttpRequest for that URL.
     */
    public HttpRequest getDefaultRequestFor(String url);

}
