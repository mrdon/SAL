package com.atlassian.sal.fisheye.trusted;

import com.atlassian.sal.core.trusted.CertificateFactory;
import com.atlassian.security.auth.trustedapps.EncryptedCertificate;
import com.atlassian.security.auth.trustedapps.TrustedApplicationsManager;

public class FisheyeCertificateFactory implements CertificateFactory
{
    private final TrustedApplicationsManager trustedApplicationsManager;

    public FisheyeCertificateFactory(TrustedApplicationsManager trustedApplicationsManager)
    {
        this.trustedApplicationsManager = trustedApplicationsManager;
    }

    public EncryptedCertificate createCertificate(String username)
    {
        return trustedApplicationsManager.getCurrentApplication().encode(username);
    }

}
