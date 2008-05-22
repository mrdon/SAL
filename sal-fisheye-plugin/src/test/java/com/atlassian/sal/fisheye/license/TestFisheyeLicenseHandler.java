package com.atlassian.sal.fisheye.license;

import junit.framework.TestCase;
import com.mockobjects.dynamic.Mock;
import com.cenqua.fisheye.config.RootConfig;
import com.cenqua.fisheye.config1.ConfigDocument;
import com.cenqua.fisheye.config1.LicenseType;
import com.cenqua.fisheye.AppConfig;

public class TestFisheyeLicenseHandler extends TestCase
{
    private Mock mockLicenseType;
    private FisheyeLicenseHandler fisheyeLicenseHandler;

    public void setUp()
    {
        // Setup Fisheye so it will return a mock license type.... Yes, painful.
        RootConfig rootConfig = new RootConfig();
        AppConfig.resetConfig(rootConfig);
        Mock mockConfigDocument = new Mock(ConfigDocument.class);
        rootConfig.setConfig((ConfigDocument) mockConfigDocument.proxy());
        Mock mockConfig = new Mock(ConfigDocument.Config.class);
        mockConfigDocument.expectAndReturn("getConfig", mockConfig.proxy());
        mockLicenseType = new Mock(LicenseType.class);
        mockConfig.expectAndReturn("getLicense", mockLicenseType.proxy());
        fisheyeLicenseHandler = new FisheyeLicenseHandler();
    }

    public void testSetLicense() throws Exception
    {
        String license = "LICENSE";
        mockLicenseType.expect("setCrucible", license);
        mockLicenseType.expect("setFisheye", license);
        fisheyeLicenseHandler.setLicense(license);
        mockLicenseType.verify();
    }

}
