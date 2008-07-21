package com.atlassian.sal.jira.license;

import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.sal.core.license.AbstractLicenseHandler;
import com.atlassian.jira.web.action.util.JiraLicenseUtils;

/**
 * Jira implementation of license handler
 */
public class JiraLicenseHandler extends AbstractLicenseHandler implements LicenseHandler
{
    /**
     * Sets the license, going through the regular validation steps as if you used the web UI
     *
     * @param license The license string
     */
    protected void setValidatedLicense(String license)
    {
        JiraLicenseUtils.setLicense(license);
    }
}
