package com.atlassian.sal.api.http;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This is a wrapper around an InputStream that limits the amount of data that can be returned. This allows admins to
 * set maximum download sizes for retrieved content, so that people can't, say, point the RSS macro at an ISO file
 * and cause the system to collapse painfully.
 *
 * If you try to retrieve more than the maximum allowed size, the read() methods will fail with an IOException
 */
final public class SizeLimitedInputStream extends InputStream
{
    private final int maximumLength;
    private final InputStream wrappedInputStream;

    private long currentLength;

    /**
     * Constructs a new <code>SizeLimitedInputStream</code>.
     *
     * @param wrappedInputStream the input stream which will be wrapped
     * @param maxBytesToRead the limit on the amount of data which can be retrieved from the input stream
     */
    public SizeLimitedInputStream(InputStream wrappedInputStream, int maxBytesToRead)
    {
        // Because we have to do a lot of byte-by-byte reading, we need a buffer.
        this.wrappedInputStream = new BufferedInputStream(wrappedInputStream);
        this.maximumLength = maxBytesToRead;
    }

    /**
     * Reads a byte from the wrapped input stream, checking that the maximum amount hasn't been read yet.
     *
     * @return the next byte from the wrapped input stream
     * @throws IOException if an attempt is made to retrieve data from beyond the maximum length
     */
    public int read() throws IOException
    {
        if (++currentLength > maximumLength)
            throw new IOException("Too much data retrieved: " + currentLength);

        return wrappedInputStream.read();
    }

    /**
     * Moves the current position in the file by <code>n</code> bytes, checking that the position is not set beyond the
     * allowed limit.
     *
     * @param n the number of bytes to skip
     * @return the acutal number of bytes skipped, which may be less than the requested amount
     * @throws IOException if too many bytes are skipped
     */
    @Override
    public long skip(long n) throws IOException
    {
        if (currentLength + n > maximumLength)
            throw new IOException("Too much data retrieved: " + currentLength + n);

        long actualSkipped = wrappedInputStream.skip(n);
        currentLength += actualSkipped;

        return actualSkipped;
    }

    /**
     * Closes the underlying input stream. This method delegates directly to the <code>close()</code> method of the
     * underlying input stream.
     *
     * @throws IOException if the stream could not be closed
     */
    @Override
    public void close() throws IOException
    {
        wrappedInputStream.close();
    }

    /**
     * Returns the number of bytes that can be read from this stream before a read can block. A return of 0 indicates
     * that blocking might (or might not) occur on the very next read attempt. This method delegates directly to the
     * underlying input stream.
     *
     * @return the number of bytes that can be read from this stream before a read can block. A return of 0 indicates
     * that blocking might (or might not) occur on the very next read attempt.
     * @throws IOException
     */
    @Override
    public int available() throws IOException
    {
        return wrappedInputStream.available();
    }

    /**
     * This method marks a position in the input to which the stream can be "reset" by calling the reset() method. This
     * method delegates directly to the underlying input stream.
     *
     * @param readlimit the number of bytes that can be read from the stream after setting the mark before the mark
     * becomes invalid. For example, if called with a read limit of 10, then when 11 bytes of data are read from the
     * stream before the <code>reset()</code> method is called, then the mark is invalid and the stream object instance
     * is not required to remember the mark.
     */
    @Override
    public synchronized void mark(int readlimit)
    {
        wrappedInputStream.mark(readlimit);
    }

    /**
     * This method resets a stream to the point where the <code>mark()</code> method was called. Any bytes that were
     * read after the mark point was set will be re-read during subsequent reads. This method delegates directly to the
     * underlying input stream.
     *
     * @throws IOException if the underlying stream does not support marking and resetting
     */
    @Override
    public synchronized void reset() throws IOException
    {
        wrappedInputStream.reset();
    }

    /**
     * Returns true if the underlying stream supports marking and resetting, which can be used to remember and return to
     * a specific point in a stream.
     *
     * @return true if the underlying stream supports marking and resetting, false otherwise
     */
    @Override
    public boolean markSupported()
    {
        return wrappedInputStream.markSupported();
    }
}
