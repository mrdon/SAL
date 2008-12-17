package com.atlassian.sal.core.net.auth;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;

import com.atlassian.sal.core.trusted.CertificateFactory;
import com.atlassian.security.auth.trustedapps.EncryptedCertificate;
import com.atlassian.security.auth.trustedapps.TrustedApplicationUtils;

public class TrustedTokenAuthenticator implements HttpClientAuthenticator
{
    private final EncryptedCertificate userCertificate;

    public TrustedTokenAuthenticator(String username, CertificateFactory certificateFactory)
    {
        if (username != null && !username.equals(""))
        {
            this.userCertificate = certificateFactory.createCertificate(username);
        } else
        {
            this.userCertificate = null;
        }
    }

    /**
     * @param httpClient The client to process
     * @param method The method type
     */
    public void process(HttpClient httpClient, HttpMethod method)
    {
        if (userCertificate!=null && userCertificate.getID() != null && !"".equals(userCertificate.getID().trim()))
        {
            method.setRequestHeader(TrustedApplicationUtils.Header.Request.ID, userCertificate.getID());
            method.setRequestHeader(TrustedApplicationUtils.Header.Request.SECRET_KEY, userCertificate.getSecretKey());
            method.setRequestHeader(TrustedApplicationUtils.Header.Request.CERTIFICATE, userCertificate.getCertificate());
        }
    }

}
