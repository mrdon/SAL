package com.atlassian.sal.api.http;

import java.io.Serializable;

/**
 * A storage class for information about http timeouts and whether or not a connection is permitted at all.
 */
public final class HttpParameters implements Serializable
{

    /**
     * The default time to wait without retrieving data from the remote connection
     */
    public static final int DEFAULT_SOCKET_TIMEOUT=10000;

    /**
     * The default time allowed for establishing a connection
     */
    public static final int DEFAULT_CONNECTION_TIMEOUT=10000;

    /**
     * Timeout for establishment of initial connection
     */
    private final int connectionTimeout;

    /**
     * Specifies how long to wait without retrieving any data from the remote connection
     */
    private final int socketTimeout;

    /**
     * Whether or not to allow external connections at all
     */
    private final boolean enabled;

    /**
     * Creates a new <code>HttpParameters</code> with default data and with enabled set to true
     */
    public HttpParameters()
    {
        this.connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
        this.socketTimeout = DEFAULT_SOCKET_TIMEOUT;
        this.enabled = true;
    }

    /**
     * Constructs a new HttpParameters object with the specified parameters
     *
     * @param connectionTimeout specifies time allowed when attempting to establish initial connection
     * @param socketTimeout specifies how long to wait without retrieving any data from the remote connection
     * @param enabled specifies whether or not to allow an external connection at all
     */
    public HttpParameters(int connectionTimeout, int socketTimeout, boolean enabled)
    {
        this.connectionTimeout = connectionTimeout;
        this.socketTimeout = socketTimeout;
        this.enabled = enabled;
    }


    /**
     *
     * @return the time allowed for establishment of an initial connection
     */
    public final int getConnectionTimeout()
    {
        return connectionTimeout;
    }

    /**
     *
     * @return the time to wait when attempting to retrieve data from a remote connection
     */
    public final int getSocketTimeout()
    {
        return socketTimeout;
    }

    /**
     *
     * @return true if connectiong to external areas is allowed, false otherwise
     */
    public final boolean isEnabled()
    {
        return enabled;
    }

    public final String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(this.connectionTimeout);
        sb.append('\n');
        sb.append(this.socketTimeout);
        sb.append('\n');
        sb.append(this.enabled);

        return sb.toString();
    }

    public boolean equals(Object o)
    {
        if(o == null)
            return false;

        if(! (o instanceof HttpParameters))
            return false;

        HttpParameters other = (HttpParameters) o;

        return(other.enabled == this.enabled
                && other.connectionTimeout == this.connectionTimeout
                && other.socketTimeout == this.socketTimeout);

    }

    public int hashCode() {
        int result;
        result = connectionTimeout;
        result = 31 * result + socketTimeout;
        result = 37 * result + (enabled ? 1 : 0);
        return result;
    }

//    /**
//     * Decodes a string into an HttpParameters
//     */
//    public HttpParameters(String s)
//    {
//        int index;
//        boolean enabled;
//        int connectionTimeout;
//        int socketTimeout;
//
//        try
//        {
//            index = s.indexOf('\n', 0);
//            String line1 = s.substring(0, index);
//
//            s = s.substring(index+1);
//            index = s.indexOf('\n', 0);
//            String line2 = s.substring(0, index);
//
//            s = s.substring(index+1);
//
//            try
//            {
//                connectionTimeout = Integer.decode(line1);
//            }
//            catch(NumberFormatException e)
//            {
//                connectionTimeout = HttpParameters.DEFAULT_CONNECTION_TIMEOUT;
//            }
//            try
//            {
//                socketTimeout = Integer.decode(line2);
//            }
//            catch(NumberFormatException e)
//            {
//                socketTimeout = HttpParameters.DEFAULT_SOCKET_TIMEOUT;
//            }
//
//            enabled = s.equalsIgnoreCase("true");
//        }
//        catch(IndexOutOfBoundsException e)
//        {
//            enabled = true;
//            connectionTimeout = HttpParameters.DEFAULT_CONNECTION_TIMEOUT;
//            socketTimeout = HttpParameters.DEFAULT_SOCKET_TIMEOUT;
//
//        }
//
//        this.connectionTimeout = connectionTimeout;
//        this.socketTimeout = socketTimeout;
//        this.enabled = enabled;
//    }

}
