package com.atlassian.sal.api.net;

/**
 * {@link HttpClientRequestFactory} implements this interface.
 * The rest plugin provides an implementation that allows marshalling of entities.
 *
 * @since 2.1
 */
public interface NonMarshallingRequestFactory<T extends Request<?, ?>> extends RequestFactory<T>
{
}
