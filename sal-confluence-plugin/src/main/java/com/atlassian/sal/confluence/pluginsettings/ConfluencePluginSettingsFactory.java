package com.atlassian.sal.confluence.pluginsettings;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;

public class ConfluencePluginSettingsFactory implements PluginSettingsFactory
{
    private BandanaManager bandanaManager;
    private TransactionTemplate txTemplate;

    public PluginSettings createSettingsForKey(String key)
    {
        final ConfluenceBandanaContext ctx = new ConfluenceBandanaContext(key);
        return new ConfluencePluginSettings(bandanaManager, ctx, txTemplate);
    }

    public PluginSettings createGlobalSettings()
    {
        return createSettingsForKey(null);
    }

    public void setBandanaManager(BandanaManager bandanaManager)
    {
        this.bandanaManager = bandanaManager;
    }

    public void setTxTemplate(TransactionTemplate txTemplate)
    {
        this.txTemplate = txTemplate;
    }
}
