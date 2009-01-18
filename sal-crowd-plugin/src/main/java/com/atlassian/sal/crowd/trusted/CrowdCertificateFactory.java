package com.atlassian.sal.crowd.trusted;

import com.atlassian.sal.core.trusted.CertificateFactory;
import com.atlassian.security.auth.trustedapps.EncryptedCertificate;

public class CrowdCertificateFactory implements CertificateFactory
{

    public EncryptedCertificate createCertificate(final String username)
    {
        throw new UnsupportedOperationException("Crowd does not support trusted application authentication");
    }
}
