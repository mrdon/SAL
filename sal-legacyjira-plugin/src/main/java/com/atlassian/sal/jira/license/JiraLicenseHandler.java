package com.atlassian.sal.jira.license;

import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.jira.web.action.util.JiraLicenseUtils;

/**
 * Jira implementation of license handler
 */
public class JiraLicenseHandler implements LicenseHandler
{
    /**
     * Sets the license, going through the regular validation steps as if you used the web UI
     *
     * @param license The license string
     */
    public void setLicense(String license)
    {
        if (JiraLicenseUtils.setLicense(license) == null)
        {
            throw new IllegalArgumentException("Specified license was invalid.");
        }
    }
}
