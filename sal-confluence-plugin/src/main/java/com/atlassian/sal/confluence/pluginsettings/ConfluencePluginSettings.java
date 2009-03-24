package com.atlassian.sal.confluence.pluginsettings;

import java.util.List;
import java.util.Properties;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import org.apache.commons.lang.Validate;

public class ConfluencePluginSettings implements PluginSettings
{
    private final BandanaManager bandanaManager;
    private final ConfluenceBandanaContext ctx;

    public ConfluencePluginSettings(final BandanaManager bandanaManager, final ConfluenceBandanaContext ctx)
    {
        this.bandanaManager = bandanaManager;
        this.ctx = ctx;
    }

    public Object put(final String key, final Object val)
    {
        Validate.notNull(key, "The plugin settings key cannot be null");
    	if ((val instanceof Properties) || (val instanceof List)  || (val instanceof String) || (val == null))
		{
    		final Object removed = bandanaManager.getValue(ctx, key);
    		bandanaManager.setValue(ctx, key, val);
    		return removed;
		}
    	else
		{
            throw new IllegalArgumentException("Property type: "+val.getClass()+" not supported");
		}
    }

    public Object get(final String key)
    {
        Validate.notNull(key, "The plugin settings key cannot be null");
        return bandanaManager.getValue(ctx, key);
    }

    public Object remove(final String key)
    {
        Validate.notNull(key, "The plugin settings key cannot be null");
        return put(key, null);
    }
}
