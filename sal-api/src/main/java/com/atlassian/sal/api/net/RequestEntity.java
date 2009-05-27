package com.atlassian.sal.api.net;

import java.io.OutputStream;
import java.io.IOException;

/**
 * An entity that can be written to a request output stream
 */
public interface RequestEntity
{
    /**
     * Writes the request entity to the given stream
     *
     * @param outputStream the outputStream to write the request to
     * @throws IOException if an error occurs while writing the entity 
     */
    void writeRequest(OutputStream outputStream) throws IOException;

    /**
     * Gets the request entity's length
     * 
     * @return The request entities length
     */
    long getContentLength();

    /**
     * Gets the request entities content type
     *
     * @return The request entities content type
     */
    String getContentType();
}
