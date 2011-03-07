package com.atlassian.sal.bamboo.pluginsettings;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.bamboo.bandana.BambooBandanaContext;
import org.apache.commons.lang.Validate;

import java.util.Properties;
import java.util.List;
import java.util.Map;

public class BambooPluginSettings implements PluginSettings
{
    // ------------------------------------------------------------------------------------------------------- Constants
    // ------------------------------------------------------------------------------------------------- Type Properties
    private final BambooBandanaContext ctx;
    private final boolean isDeveloperMode = Boolean.getBoolean("atlassian.dev.mode");
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
        Validate.notNull(key, "The plugin settings key cannot be null");
        Validate.isTrue(key.length() <= 255, "The plugin settings key cannot be more than 255 characters");
        if (isDeveloperMode)
        {
            Validate.isTrue(key.length() <= 100, "The plugin settings key cannot be more than 100 characters in developer mode");
        }
        if ((val instanceof Properties) || (val instanceof List) || (val instanceof String) || (val instanceof Map) || (val == null))
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
        Validate.notNull(key, "The plugin settings key cannot be null");
        return bandanaManager.getValue(ctx, key, false);
    }

    public Object remove(final String key)
    {
        Validate.notNull(key, "The plugin settings key cannot be null");
        final Object removed = bandanaManager.getValue(ctx, key, false);
        bandanaManager.setValue(ctx, key, null);
        return removed;
    }
    // ------------------------------------------------------------------------------------------------- Helper Methods
    // -------------------------------------------------------------------------------------- Basic Accessors / Mutators
}
