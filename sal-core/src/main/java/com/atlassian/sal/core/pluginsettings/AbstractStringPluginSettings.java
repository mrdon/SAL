package com.atlassian.sal.core.pluginsettings;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 * PluginSettings implementation for datastores that only support Strings.  Handles converting Strings into Lists and
 * Properties objects using a '#TYPE_IDENTIFIER' header on the string.
 */
public abstract class AbstractStringPluginSettings implements PluginSettings
{
    private static final Logger log = Logger.getLogger(AbstractStringPluginSettings.class);
    
    private static final String PROPERTIES_ENCODING = "ISO8859_1";
    private static final String PROPERTIES_IDENTIFIER = "java.util.Properties";
    private static final String LIST_IDENTIFIER = "#java.util.List";
    private static final String MAP_IDENTIFIER = "#java.util.Map";
    private static final String VERTICAL_TAB = "\f";

    @SuppressWarnings("unchecked")
	public Object put(String key, Object val)
    {
        Validate.notNull(key, "The plugin settings key cannot be null");
        if (val == null)
            return removeActual(key);

        if (val instanceof Properties)
        {
            final ByteArrayOutputStream bout = new ByteArrayOutputStream();
            try
            {

                final Properties properties = (Properties) val;
                properties.store(bout, PROPERTIES_IDENTIFIER);
                putActual(key, new String(bout.toByteArray(), PROPERTIES_ENCODING));
            } catch (final IOException e)
            {
                throw new IllegalArgumentException("Unable to serialize properties", e);
            }
        }
        else if (val instanceof String)
            putActual(key, (String)val);
        else if (val instanceof List)
        {
            final StringBuilder sb = new StringBuilder();
            sb.append(LIST_IDENTIFIER).append("\n");
            for (final Iterator i = ((List)val).iterator(); i.hasNext(); )
            {
                sb.append(i.next().toString());
                if (i.hasNext())
                    sb.append('\n');
            }
            putActual(key, sb.toString());
        }
        else if (val instanceof Map)
        {
            final StringBuilder sb = new StringBuilder();
            sb.append(MAP_IDENTIFIER).append("\n");
            for (final Iterator<Entry> i = ((Map)val).entrySet().iterator(); i.hasNext(); )
            {
                final Entry entry = i.next();
                sb.append(entry.getKey().toString());
                sb.append(VERTICAL_TAB);
                sb.append(entry.getValue().toString());
                if (i.hasNext())
                    sb.append('\n');
            }
            putActual(key, sb.toString());
        }
        else
            throw new IllegalArgumentException("Property type: "+val.getClass()+" not supported");
        return val;
    }


    public Object get(String key)
    {
        Validate.notNull(key, "The plugin settings key cannot be null");
        final String val = getActual(key);
        if (val != null && val.startsWith("#"+PROPERTIES_IDENTIFIER))
        {
            final Properties p = new Properties();
            try
            {
                p.load(new ByteArrayInputStream(val.getBytes(PROPERTIES_ENCODING)));
            } catch (final IOException e)
            {
                throw new IllegalArgumentException("Unable to deserialize properties", e);
            }
            return p;
        } else if (val != null && val.startsWith(LIST_IDENTIFIER))
        {
            final ArrayList<String> list = new ArrayList<String>();
            final String[] items = val.split("\n");
            list.addAll(Arrays.asList(items).subList(1, items.length));

            return list;
        } else if (val != null && val.startsWith(MAP_IDENTIFIER))
        {
            String nval = val.substring(MAP_IDENTIFIER.length()+1);
            final HashMap<String, String> map = new HashMap<String, String>();
            final String[] items = nval.split("\n");
            for (String item : items) {
                String[] pair = item.split(VERTICAL_TAB);
                if(pair.length != 2)
                {
                    log.error("Could not parse map element: << " + item + " >> \n" +
                            "Full list: \n" + nval);
                }
                
                map.put(pair[0], pair[1]);
            }

            return map;
        } else
        {
            return val;
        }
    }

    protected abstract void putActual(String key, String val);
    protected abstract String getActual(String key);

    /**
     * Do the removal
     * @param key The key to remove
     * @return The value that was removed
     */
    protected abstract Object removeActual(String key);


    public Object remove(String key)
    {
        Validate.notNull(key, "The plugin settings key cannot be null");
        return put(key, null);
    }
}
