package com.atlassian.sal.api.http;

import junit.framework.TestCase;

import java.io.IOException;
import java.io.InputStream;

import com.atlassian.sal.api.http.SizeLimitedInputStream;

public class TestSizeLimitedInputStream extends TestCase
{

    public void testReadOver() throws IOException
    {
        final int size = 10;
        final int max = 7;

        final InputStream stream = new MockInputStream(size);
        final SizeLimitedInputStream sliz = new SizeLimitedInputStream(stream, max);

        for(int i = 0; i < max; i++)
        {
            sliz.read();
        }

        try
        {
            sliz.read();
            fail("exception not thrown");
        }
        catch(Exception e)
        {
        }
    }

    public void testMarkReset() throws IOException
    {
        final int size = 100;
        final int max = 50;

        final InputStream stream = new MockInputStream(size, true);
        final SizeLimitedInputStream sliz = new SizeLimitedInputStream(stream, max);

        assertTrue(sliz.markSupported());

        for(int i = 0; i < 10; i++)
        {
            sliz.read();
        }

        sliz.mark(4000);
        int c = sliz.read();
        
        for(int i = 0; i < 10; i++)
        {
            sliz.read();
        }
        
        try
        {
            sliz.reset();
        }
        catch(IOException e)
        {
            fail("Exception should not be thrown here. SizeLimitedInputStream failed to delegate to subclass correctly.");
        }

        int c2 = sliz.read();

        assertEquals(c, c2);
    }

    private static final class MockInputStream extends InputStream
    {
        final long sizeInBytes;
        long currentPosition = 0;
        final boolean supportsMark;
        long markedPosition = 0;

        MockInputStream(int sizeInBytes)
        {
            this.sizeInBytes = sizeInBytes;
            this.supportsMark = false;
        }

        MockInputStream(int sizeInBytes, boolean markSupported)
        {
            this.sizeInBytes = sizeInBytes;
            this.supportsMark = markSupported;
        }

        public int read() throws IOException
        {
            if(currentPosition >= sizeInBytes)
                return -1;

            currentPosition++;
            return (int)this.currentPosition;
        }

        public boolean markSupported()
        {
            return this.supportsMark;
        }

        public long skip(long n)
        {
            if(this.currentPosition + n >= this.sizeInBytes)
            {
                long x = this.sizeInBytes - this.currentPosition - 1;
                this.currentPosition = this.sizeInBytes - 1;
                return x;
            }

            this.currentPosition += n;
            return n;
        }

        public void mark(int readLimit)
        {
            this.markedPosition = this.currentPosition;
        }

        public void reset() throws IOException
        {
            if(!this.supportsMark)
                throw new IOException("mark not suppoted");

            this.currentPosition = this.markedPosition;
        }
    }
}
