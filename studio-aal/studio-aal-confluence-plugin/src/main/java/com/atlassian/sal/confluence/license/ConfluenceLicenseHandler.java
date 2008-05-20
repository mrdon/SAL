package com.atlassian.sal.confluence.license;

import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.config.ConfigurationException;
import com.atlassian.confluence.event.events.admin.LicenceUpdatedEvent;
import com.atlassian.confluence.setup.ConfluenceBootstrapConstants;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.UserChecker;
import com.atlassian.event.EventManager;
import com.atlassian.license.*;
import com.atlassian.license.decoder.LicenseDecoder;
import com.atlassian.sal.api.component.ComponentLocator;
import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.spring.container.ContainerManager;
import com.opensymphony.util.TextUtils;

import java.util.Date;

/**
 * Sets the license for Confluence
 */
public class ConfluenceLicenseHandler implements LicenseHandler
{
    /**
     * Sets the license, going through the validation used for the web UI (copy/pasted unfortunately).  Errors are ignored
     * as with the JIRA implementation, although this should be improved and made consistent.
     *
     * @param licenseString The license String
     */
    public void setLicense(String licenseString)
    {
        EventManager eventManager = ComponentLocator.getComponent(EventManager.class);
        UserChecker userChecker = ComponentLocator.getComponent(UserChecker.class);

        // FIXME: cannot use ComponentLocator as it doesn't seem to search the bootstrap confluence context.
        ApplicationConfiguration applicationConfig = (ApplicationConfiguration) ContainerManager.getInstance().getContainerContext().getComponent("applicationConfig");
        LicensePair pair;
        try
        {
            pair = new LicensePair(licenseString);
        }
        catch (LicenseException e)
        {
            //log.warn("The license you specified was invalid.");
            //addFieldError("licenseString", getText("license.invalid.error", new Object[] {e.getMessage()}));
            return;
        }

        License updatedLicense = LicenseDecoder.getLicense(pair, ConfluenceBootstrapConstants.DEFAULT_LICENSE_REGISTRY_KEY);

        if (updatedLicense == null)
        {
            //log.warn("The license you specified was invalid.");
            //addFieldError("licenseString", getText("license.invalid.and.not.updated"));
            //return ERROR;
            return;
        }

        Date supportPeriodEndDate = new Date(LicenseUtils.getSupportPeriodEnd(updatedLicense));
        String partnerErrorMessage = GeneralUtil.checkPartnerDetails(updatedLicense, GeneralUtil.getBuildPartner());

        if (GeneralUtil.getBuildDate().after(supportPeriodEndDate))
        {
            //log.warn("The license you specified was invalid.");
            //addFieldError("licenseString", getText("confluence.support.for.license.ended", new Object[] {supportPeriodEndDateString}));
            //return ERROR;
            return;
        }

        if (TextUtils.stringSet(partnerErrorMessage))
        {
            //log.fatal("License does not match partner");
            //addFieldError("licenseString", partnerErrorMessage);
            //return ERROR;
            return;
        }

        LicenseManager.getInstance().setLicense(licenseString, ConfluenceBootstrapConstants.DEFAULT_LICENSE_REGISTRY_KEY);
        try
        {
            applicationConfig.save();
        } catch (ConfigurationException e)
        {
            // TODO: handle this better
            throw new RuntimeException("Cannot save application configuration", e);
        }
        eventManager.publishEvent(new LicenceUpdatedEvent(this, updatedLicense));

        userChecker.resetResult();
    }
}
