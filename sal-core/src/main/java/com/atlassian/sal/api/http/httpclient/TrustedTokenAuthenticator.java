package com.atlassian.sal.api.http.httpclient;

import com.atlassian.sal.api.http.HttpRequest;
import com.atlassian.security.auth.trustedapps.EncryptedCertificate;
import com.atlassian.security.auth.trustedapps.TrustedApplicationUtils;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * An <code>Authenticator</code> that attaches a token to an HTTP request and allows for status reporting on an executed
 * request
 */
final class TrustedTokenAuthenticator extends HttpClientAuthenticator
{

    /**
     * Certificate used to provide credentials for authentication.
     */
    private final EncryptedCertificate userCertificate;

    /**
     * Obtain a new <code>TrustedTokenAuthenticator</code>
     *
     * @param userCertificate will be used to provide
     */
    TrustedTokenAuthenticator(EncryptedCertificate userCertificate)
    {
        this.userCertificate = userCertificate;
    }

    /**
     * Generates an <code>HttpMethod</code> which has headers set with the user's credentials
     *
     * @return a method that has headers set with credentials encrypted with the certificate supplied at construction
     */
    public HttpMethod makeMethod(HttpRequest request)
    {
        HttpMethod method;

        // TODO use TrustedApplicationUtils#addHeaders
        method = HttpClientHttpRequest.createMethod(request.getUrl(), request);
        if (userCertificate!=null && userCertificate.getID() != null && !"".equals(userCertificate.getID().trim()))
        {
            method.setRequestHeader(TrustedApplicationUtils.Header.Request.ID, userCertificate.getID());
            method.setRequestHeader(TrustedApplicationUtils.Header.Request.SECRET_KEY, userCertificate.getSecretKey());
            method.setRequestHeader(TrustedApplicationUtils.Header.Request.CERTIFICATE, userCertificate.getCertificate());
        }

        return method;
    }

    /**
     * Return the status of a trusted application response
     *
     * @param httpMethod executed HttpClient method
     * @return trusted connection status or null if the request was not a trusted application request
     */
    public TrustedConnectionStatus getTrustedConnectionStatus(HttpMethod httpMethod)
    {
        if (!httpMethod.hasBeenUsed())
            throw new IllegalStateException("Method has not been executed");

        if (!isTrustedConnectionRequest(httpMethod))
            return null;

        return buildStatus(httpMethod);
    }

    private static boolean isTrustedConnectionRequest(HttpMethod method)
    {
        return method.getRequestHeader(TrustedApplicationUtils.Header.Request.ID) != null;
    }


    private TrustedConnectionStatus buildStatus(HttpMethod httpMethod)
    {
        // Check for trusted connection support
        Header[] statusHeaders = httpMethod.getResponseHeaders(TrustedApplicationUtils.Header.Response.STATUS);
        boolean trustSupported = (!(statusHeaders == null || statusHeaders.length == 0));
        if (!trustSupported)
            return TrustedConnectionStatus.UNSUPPORTED;

        // Check for any connection errors
        Header[] headers = httpMethod.getResponseHeaders(TrustedApplicationUtils.Header.Response.ERROR);
        if (headers == null || headers.length == 0)
            return TrustedConnectionStatus.SUCCESS;

        TrustedConnectionStatusBuilder builder = new TrustedConnectionStatusBuilder();
        for (Header header : headers)
        {
            builder.addTrustedConnectionError(header.getValue());
        }

        return builder.getStatus();
    }

    private static class TrustedConnectionStatusBuilder
    {
        private final List<String> trustedConnectionErrors = new LinkedList<String>();

        private static final String UNRECOGNIZED_APP = "Unrecognized application";
        private static final String UNRECOGNIZED_USER = "Unrecognized user";

        public TrustedConnectionStatusBuilder addTrustedConnectionError(String error)
        {
            this.trustedConnectionErrors.add(error);
            return this;
        }

        public TrustedConnectionStatus getStatus()
        {
            if (trustedConnectionErrors.isEmpty())
                return TrustedConnectionStatus.SUCCESS;

            boolean appRecognized = !trustedConnectionErrors.contains(UNRECOGNIZED_APP);
            boolean userRecognized = appRecognized && !trustedConnectionErrors.contains(UNRECOGNIZED_USER);

            return new TrustedConnectionStatus(userRecognized, appRecognized, true, trustedConnectionErrors, true);
        }
    }

    /**
     * Represents the status of a trusted connection response
     */
    public final static class TrustedConnectionStatus
    {
        public static final TrustedConnectionStatus UNSUPPORTED = new TrustedConnectionStatus(false, false, false, Collections.<String>emptyList(), false);
        public static final TrustedConnectionStatus SUCCESS = new TrustedConnectionStatus(true, true, false, Collections.<String>emptyList(), true);

        private final boolean userRecognized;
        private final boolean appRecognized;
        private final boolean trustedConnectionError;
        private final List<String> trustedConnectionErrors;
        private final boolean trustSupported;

        private TrustedConnectionStatus(boolean userRecognized, boolean appRecognized, boolean trustedConnectionError, List<String> trustedConnectionErrors, boolean trustSupported)
        {
            this.userRecognized = userRecognized;
            this.appRecognized = appRecognized;
            this.trustedConnectionError = trustedConnectionError;
            this.trustedConnectionErrors = Collections.unmodifiableList(new LinkedList<String>(trustedConnectionErrors));
            this.trustSupported = trustSupported;
        }

        /**
         * @return true if the user was recognised by the server
         */
        public boolean isUserRecognized()
        {
            return userRecognized;
        }

        /**
         * @return true if the requesting application was recognised by the server (i.e. a trust relationship has been established)
         */
        public boolean isAppRecognized()
        {
            return appRecognized;
        }

        /**
         * @return true if there were any errors in processing the trusted application request
         */
        public boolean isTrustedConnectionError()
        {
            return trustedConnectionError;
        }

        /**
         * @return A list of errors reported by the server
         */
        public List<String> getTrustedConnectionErrors()
        {
            return trustedConnectionErrors;
        }

        /**
         * @return true if the server actually supports trusted application requests
         */
        public boolean isTrustSupported()
        {
            return trustSupported;
        }

        public String toString()
        {
            if (!trustSupported)
                return "Trusted connection not supported";

            if (!trustedConnectionError)
                return "Trusted connection successful";

            return "Trusted connection errors: " + trustedConnectionErrors;
        }
    }

}
