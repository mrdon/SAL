package com.atlassian.sal.core.trusted;

import com.atlassian.security.auth.trustedapps.EncryptedCertificate;

/**
 * Interface for retrieving encrypted certificate for given username.
 */
public interface CertificateFactory
{
    EncryptedCertificate createCertificate(String username);
}
