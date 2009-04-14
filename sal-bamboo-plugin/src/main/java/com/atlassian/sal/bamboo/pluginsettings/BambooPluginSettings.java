package com.atlassian.sal.bamboo.pluginsettings;

import org.apache.log4j.Logger;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.bamboo.bandana.BambooBandanaContext;

import java.util.Properties;
import java.util.List;

public class BambooPluginSettings implements PluginSettings
{
    private static final Logger log = Logger.getLogger(BambooPluginSettings.class);
    // ------------------------------------------------------------------------------------------------------- Constants
    // ------------------------------------------------------------------------------------------------- Type Properties
    private final BambooBandanaContext ctx;
    // ---------------------------------------------------------------------------------------------------- Dependencies
    private final BandanaManager bandanaManager;
    // ---------------------------------------------------------------------------------------------------- Constructors

    public BambooPluginSettings(final BandanaManager bandanaManager, final BambooBandanaContext ctx)
    {
        this.bandanaManager = bandanaManager;
        this.ctx = ctx;
    }
    // -------------------------------------------------------------------------------------------------- Public Methods

    public Object put(final String key, final Object val)
    {
        if ((val instanceof Properties) || (val instanceof List) || (val instanceof String) || (val == null))
        {
            final Object removed = bandanaManager.getValue(ctx, key, false);
            bandanaManager.setValue(ctx, key, val);
            return removed;
        }
        else
        {
            throw new IllegalArgumentException("Property type: " + val.getClass() + " not supported");
        }
    }

    public Object get(final String key)
    {
        return bandanaManager.getValue(ctx, key, false);
    }

    public Object remove(final String key)
    {
        return put(key, null);
    }
    // ------------------------------------------------------------------------------------------------- Helper Methods
    // -------------------------------------------------------------------------------------- Basic Accessors / Mutators
}
