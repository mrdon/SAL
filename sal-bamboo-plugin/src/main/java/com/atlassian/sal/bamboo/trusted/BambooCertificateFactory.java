package com.atlassian.sal.bamboo.trusted;

import org.apache.log4j.Logger;
import com.atlassian.sal.core.trusted.CertificateFactory;
import com.atlassian.security.auth.trustedapps.EncryptedCertificate;

/**
 * Bamboo does not yet support Trusted Apps.
 */
public class BambooCertificateFactory implements CertificateFactory
{
    private static final Logger log = Logger.getLogger(BambooCertificateFactory.class);
    // ------------------------------------------------------------------------------------------------------- Constants
    // ------------------------------------------------------------------------------------------------- Type Properties
    // ---------------------------------------------------------------------------------------------------- Dependencies
    // ---------------------------------------------------------------------------------------------------- Constructors
    // ----------------------------------------------------------------------------------------------- Interface Methods
    // -------------------------------------------------------------------------------------------------- Action Methods
    // -------------------------------------------------------------------------------------------------- Public Methods
    public EncryptedCertificate createCertificate(String username)
    {
        throw new UnsupportedOperationException("Bamboo does not support trusted application authentication");
    }
    // ------------------------------------------------------------------------------------------------- Helper Methods
    // -------------------------------------------------------------------------------------- Basic Accessors / Mutators
}
