package com.atlassian.sal.confluence.pluginsettings;

import java.util.List;
import java.util.Properties;
import java.util.Map;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.transaction.TransactionCallback;
import org.apache.commons.lang.Validate;

public class ConfluencePluginSettings implements PluginSettings
{
    private final BandanaManager bandanaManager;
    private final ConfluenceBandanaContext ctx;
    private final TransactionTemplate txTemplate;

    public ConfluencePluginSettings(final BandanaManager bandanaManager, final ConfluenceBandanaContext ctx, TransactionTemplate txTemplate)
    {
        this.bandanaManager = bandanaManager;
        this.ctx = ctx;
        this.txTemplate = txTemplate;
    }

    public Object put(final String key, final Object val)
    {

        Validate.notNull(key, "The plugin settings key cannot be null");
    	if ((val instanceof Properties) || (val instanceof List)  || (val instanceof String) || (val instanceof Map) || (val == null))
		{
            return txTemplate.execute(new TransactionCallback()
            {
                public Object doInTransaction()
                {
                    final Object removed = bandanaManager.getValue(ctx, key);
                    bandanaManager.setValue(ctx, key, val);
                    return removed;
                }
            });
		}
    	else
		{
            throw new IllegalArgumentException("Property type: "+val.getClass()+" not supported");
		}
    }

    public Object get(final String key)
    {
        Validate.notNull(key, "The plugin settings key cannot be null");

        return txTemplate.execute(new TransactionCallback()
        {
            public Object doInTransaction()
            {
                return bandanaManager.getValue(ctx, key);
            }
        });

    }

    public Object remove(final String key)
    {
        Validate.notNull(key, "The plugin settings key cannot be null");
        return txTemplate.execute(new TransactionCallback()
        {
            public Object doInTransaction()
            {
                return put(key, null);
            }
        });
    }
}
