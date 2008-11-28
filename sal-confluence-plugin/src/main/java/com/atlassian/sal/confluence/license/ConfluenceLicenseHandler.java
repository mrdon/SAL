package com.atlassian.sal.confluence.license;

import java.util.Date;

import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.config.ConfigurationException;
import com.atlassian.confluence.event.events.admin.LicenceUpdatedEvent;
import com.atlassian.confluence.setup.ConfluenceBootstrapConstants;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.UserChecker;
import com.atlassian.event.EventManager;
import com.atlassian.license.License;
import com.atlassian.license.LicenseException;
import com.atlassian.license.LicenseManager;
import com.atlassian.license.LicensePair;
import com.atlassian.license.LicenseUtils;
import com.atlassian.license.decoder.LicenseDecoder;
import com.atlassian.sal.api.component.ComponentLocator;
import com.atlassian.sal.api.license.LicenseHandler;
import com.opensymphony.util.TextUtils;

/**
 * Sets the license for Confluence
 */
public class ConfluenceLicenseHandler implements LicenseHandler
{

    private final ApplicationConfiguration applicationConfiguration;
	private final EventManager eventManager;

	public ConfluenceLicenseHandler(final ApplicationConfiguration applicationConfiguration, final EventManager eventManager)
	{
		this.applicationConfiguration = applicationConfiguration;
		this.eventManager = eventManager;
	}

	/**
     * Sets the license, going through the validation used for the web UI (copy/pasted unfortunately).  Errors are ignored
     * as with the JIRA implementation, although this should be improved and made consistent.
     *
     * @param licenseString The license String
     */
    public void setLicense(final String licenseString)
    {
        final UserChecker userChecker = ComponentLocator.getComponent(UserChecker.class);

        LicensePair pair;
        try
        {
            pair = new LicensePair(licenseString);
        }
        catch (final LicenseException e)
        {
            //log.warn("The license you specified was invalid.");
            //addFieldError("licenseString", getText("license.invalid.error", new Object[] {e.getMessage()}));
            return;
        }

        final License updatedLicense = LicenseDecoder.getLicense(pair, ConfluenceBootstrapConstants.DEFAULT_LICENSE_REGISTRY_KEY);

        if (updatedLicense == null)
        {
            //log.warn("The license you specified was invalid.");
            //addFieldError("licenseString", getText("license.invalid.and.not.updated"));
            //return ERROR;
            return;
        }

        final Date supportPeriodEndDate = new Date(LicenseUtils.getSupportPeriodEnd(updatedLicense));
        final String partnerErrorMessage = GeneralUtil.checkPartnerDetails(updatedLicense, GeneralUtil.getBuildPartner());

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
        	applicationConfiguration.save();
        } catch (final ConfigurationException e)
        {
            // TODO: handle this better
            throw new RuntimeException("Cannot save application configuration", e);
        }
        eventManager.publishEvent(new LicenceUpdatedEvent(this, updatedLicense));

        userChecker.resetResult();
    }
}
