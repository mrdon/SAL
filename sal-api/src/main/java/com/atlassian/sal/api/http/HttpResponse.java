package com.atlassian.sal.api.http;

import java.io.IOException;
import java.io.InputStream;

/**
 * Represents the response that is received from an http request.
 */
public interface HttpResponse
{

    /**
     *
     * @return true if the data is cached locally, false otherwise
     */
    public boolean isCached();

    /**
     *
     * @return true if the request failed, false otherwise
     */
    public boolean isFailed();

    /**
     *
     * @return true if the target url was not found, false otherwise
     */
   public  boolean isNotFound();

    /**
     *
     * @return true if access to the requested resource is not permitted, false otherwise
     */
    public boolean isNotPermitted();

    /**
     * Retrieves the input stream from which data for this request can be retrieved. Usually the <code>isFailed()</code>,
     * <code>isNotFound()</code> and <code>isNotPermitted</code> methods should be checked before calling this method.
     *
     * @return a stream containing the data retrieved by this request
     * @throws IOException if the response cannot be converted to a stream
     */
    public InputStream getResponseBody() throws IOException;

    /**
     * @return the content type of the response, as described in its header
     */
    public String getContentType();

    /**
     * @return the status of the request as a text message
     */
    public String getStatusMessage();

    /**
     * @return the status of the request as a numerical code
     */
    public int getStatusCode();

    /**
     * This method cleans up the response. It is strongly recommended that this method be called on a request once it is
     * no longer needed.
     */
    public void finish();

    /**
     * @return body of the response as a String
     * @throws IOException 
     */
    public String getResponseBodyAsString() throws IOException;

    /**
     * @return true if response is successful
     */
    public boolean isSuccessful();

}
