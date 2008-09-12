package com.atlassian.sal.confluence.pluginsettings;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

public class ConfluencePluginSettingsFactory implements PluginSettingsFactory
{
    private BandanaManager bandanaManager;

    public PluginSettings createSettingsForKey(String key)
    {
        final ConfluenceBandanaContext ctx = new ConfluenceBandanaContext(key);
        return new ConfluencePluginSettings(bandanaManager, ctx);
    }

    public PluginSettings createGlobalSettings()
    {
        return createSettingsForKey(null);
    }

    public void setBandanaManager(BandanaManager bandanaManager)
    {
        this.bandanaManager = bandanaManager;
    }

	public PluginSettings createUserSettings(String username)
	{
		throw new UnsupportedOperationException("Not yet implemented in Confluence.");
	}
}
