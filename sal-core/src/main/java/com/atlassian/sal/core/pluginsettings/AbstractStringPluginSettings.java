package com.atlassian.sal.core.pluginsettings;

import com.atlassian.sal.api.pluginsettings.PluginSettings;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * PluginSettings implementation for datastores that only support Strings.  Handles converting Strings into Lists and
 * Properties objects using a '#TYPE_IDENTIFIER' header on the string.
 */
public abstract class AbstractStringPluginSettings implements PluginSettings
{
    private static final String PROPERTIES_ENCODING = "ISO8859_1";
    private static final String PROPERTIES_IDENTIFIER = "java.util.Properties";
    private static final String LIST_IDENTIFIER = "java.util.List";

    public Object put(String key, Object val)
    {
        if (val == null)
            return removeActual(key);

        if (val instanceof Properties)
        {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            try
            {

                Properties properties = (Properties) val;
                properties.store(bout, PROPERTIES_IDENTIFIER);
                putActual(key, new String(bout.toByteArray(), PROPERTIES_ENCODING));
            } catch (IOException e)
            {
                throw new IllegalArgumentException("Unable to serialize properties", e);
            }
        }
        else if (val instanceof String)
            putActual(key, (String)val);
        else if (val instanceof List)
        {
            StringBuilder sb = new StringBuilder();
            sb.append("#java.util.List\n");
            for (Iterator i = ((List)val).iterator(); i.hasNext(); )
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

        String val = getActual(key.toString());
        if (val != null && val.startsWith("#"+PROPERTIES_IDENTIFIER))
        {
            Properties p = new Properties();
            try
            {
                p.load(new ByteArrayInputStream(val.getBytes(PROPERTIES_ENCODING)));
            } catch (IOException e)
            {
                throw new IllegalArgumentException("Unable to deserialize properties", e);
            }
            return p;
        } else if (val != null && val.startsWith("#"+LIST_IDENTIFIER))
        {
            ArrayList<String> list = new ArrayList<String>();
            String[] items = val.split("\n");
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
