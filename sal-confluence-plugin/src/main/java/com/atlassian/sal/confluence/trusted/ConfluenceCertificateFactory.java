package com.atlassian.sal.confluence.trusted;

import com.atlassian.confluence.security.trust.TrustedToken;
import com.atlassian.confluence.security.trust.TrustedTokenFactory;
import com.atlassian.sal.api.component.ComponentLocator;
import com.atlassian.sal.core.trusted.CertificateFactory;
import com.atlassian.security.auth.trustedapps.DefaultEncryptedCertificate;
import com.atlassian.security.auth.trustedapps.EncryptedCertificate;

public class ConfluenceCertificateFactory implements CertificateFactory
{
    public EncryptedCertificate createCertificate(String username)
    {
        TrustedTokenFactory trustedTokenFactory = ComponentLocator.getComponent(TrustedTokenFactory.class);
        TrustedToken token = trustedTokenFactory.getToken();
        if (token != null)
        {
            return new DefaultEncryptedCertificate(token.getApplicationId(), token.getEncodedKey(), token.getEncodedToken(), 1, null);
        } 
        return null;
    }

}
