package com.atlassian.sal.fisheye.pluginsettings;

import com.atlassian.sal.api.pluginsettings.AbstractStringPluginSettings;
import com.cenqua.fisheye.config.RootConfig;
import com.cenqua.fisheye.config1.PropertiesType;
import com.cenqua.fisheye.config1.PropertyType;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Wraps the low-level {@link com.cenqua.fisheye.config1.PropertiesType} object behind a {@link java.util.Map} facade.  Put and remove operations
 * will affect the underlying data model.
 */
public class FisheyePluginSettings extends AbstractStringPluginSettings
{
    private PropertiesType propertiesType;
    private RootConfig rootConfig;

    public FisheyePluginSettings(RootConfig config, PropertiesType set)
    {
        this.rootConfig = config;
        this.propertiesType = set;
    }

    private void storeConfig() 
    {
        try
        {
            rootConfig.saveConfig();
            // todo: should we call  AppConfig.updateLastModified()? See FE-123.
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    protected Object removeActual(String key)
    {
        int index = findPropertyType(key.toString());
        if (index > -1)
            propertiesType.removeProperty(index);
        storeConfig();
        return key;
    }

    protected void putActual(String key, String s)
    {
        String val;
        try
        {
            val = URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }
        PropertyType type;
        int index = findPropertyType(key);
        if (index > -1)
            type = propertiesType.getPropertyArray(index);
        else
        {
            type = propertiesType.addNewProperty();
            type.setName(key);
        }
        type.setValue(val);
        storeConfig();
    }

    protected String getActual(String key)
    {
        int index = findPropertyType(key);
        if (index > -1)
        {
            String value = propertiesType.getPropertyArray(index).getValue();
            try
            {
                return URLDecoder.decode(value, "UTF-8");
            } catch (UnsupportedEncodingException e)
            {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    private int findPropertyType(String key)
    {
        for (int x=0; x<propertiesType.sizeOfPropertyArray(); x++) {
            if (key.equals(propertiesType.getPropertyArray(x).getName()))
                return x;
        }
        return -1;
    }

}
