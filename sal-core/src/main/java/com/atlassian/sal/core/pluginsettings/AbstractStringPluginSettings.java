package com.atlassian.sal.core.pluginsettings;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.atlassian.sal.api.pluginsettings.PluginSettings;

/**
 * PluginSettings implementation for datastores that only support Strings.  Handles converting Strings into Lists and
 * Properties objects using a '#TYPE_IDENTIFIER' header on the string.
 */
public abstract class AbstractStringPluginSettings implements PluginSettings
{
    private static final String PROPERTIES_ENCODING = "ISO8859_1";
    private static final String PROPERTIES_IDENTIFIER = "java.util.Properties";
    private static final String LIST_IDENTIFIER = "java.util.List";

    @SuppressWarnings("unchecked")
	public Object put(String key, Object val)
    {
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
            sb.append("#java.util.List\n");
            for (final Iterator i = ((List)val).iterator(); i.hasNext(); )
            {
                sb.append(i.next().toString());
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
        } else if (val != null && val.startsWith("#"+LIST_IDENTIFIER))
        {
            final ArrayList<String> list = new ArrayList<String>();
            final String[] items = val.split("\n");
            for (int x=1; x<items.length; x++)
                list.add(items[x]);

            return list;
        } else
        {
            return val;
        }
    }

    protected abstract void putActual(String key, String val);
    protected abstract String getActual(String key);
    protected abstract Object removeActual(String key);


    public Object remove(String key)
    {
        return put(key, null);
    }
}
