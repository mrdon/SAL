package com.atlassian.sal.trustedapps;

import com.atlassian.sal.core.trusted.CertificateFactory;
import com.atlassian.security.auth.trustedapps.EncryptedCertificate;
import com.atlassian.plugin.StateAware;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.framework.BundleContext;
import org.apache.log4j.Logger;

/**
 * This factory has a weak classloading dependency on {@link com.atlassian.security.auth.trustedapps.api.CertificateFactory}
 * through an OSGi ServiceTracker, hence the trusted apps plugin does not need to be installed for this class to work
 * (it will simply throw an exception saying that trusted apps isn't supported).
 */
public class TrustedAppsPluginCertificateFactory implements CertificateFactory, StateAware
{
    private static final Logger log = Logger.getLogger(TrustedAppsPluginCertificateFactory.class);
    private static final String CERTIFICATE_FACTORY = "com.atlassian.security.auth.trustedapps.api.CertificateFactory";
    private ServiceTracker serviceTracker;
    private final BundleContext bundleContext;

    public TrustedAppsPluginCertificateFactory(BundleContext bundleContext)
    {
        this.bundleContext = bundleContext;
        serviceTracker = new ServiceTracker(bundleContext, CERTIFICATE_FACTORY, null);
        serviceTracker.open();
    }

    public EncryptedCertificate createCertificate(String username)
    {
        if (serviceTracker != null)
        {
            try
            {
                com.atlassian.security.auth.trustedapps.api.CertificateFactory certificateFactory =
                    (com.atlassian.security.auth.trustedapps.api.CertificateFactory) serviceTracker.getService();
                if (certificateFactory != null)
                {
                    return certificateFactory.createCertificate(username);
                }
            }
            catch (NoClassDefFoundError ncdfe)
            {
                // This probably won't happen, if trustedapps isn't installed then this method will return null,
                // so no cast will be attempted, so the class won't need to be loaded, and so this error won't be
                // thrown. Whether the class is loaded though may be platform dependent, I can't see anything in the
                // Java Language Specification that indicates what should happen, so for safety, we ignore this
                // exception here as it indicates that trusted apps is not installed.  It could also mean that the
                // class hasn't been wired appropriately, which the OSGi framework will warn us about anyway.
            }
            catch (ClassCastException cce)
            {
                // This exception is possible if CertificateFactory has been uninstalled and installed. Warn.
                log.warn(
                    "A CertificateFactory was found, but a ClassCastException was thrown when attempting to cast it.",
                    cce);
            }
        }
        throw new UnsupportedOperationException("Trusted apps support is not installed.");
    }

    public void enabled()
    {
        if (serviceTracker == null)
        {
            serviceTracker = new ServiceTracker(bundleContext, CERTIFICATE_FACTORY, null);
            serviceTracker.open();
        }
    }

    public void disabled()
    {
        serviceTracker.close();
        serviceTracker = null;
    }
}
