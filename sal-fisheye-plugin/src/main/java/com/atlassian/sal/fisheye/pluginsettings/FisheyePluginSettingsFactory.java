package com.atlassian.sal.fisheye.pluginsettings;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.cenqua.fisheye.AppConfig;
import com.cenqua.fisheye.config.RootConfig;
import com.cenqua.fisheye.config1.PropertiesType;
import com.cenqua.fisheye.config1.RepositoryType;
import com.cenqua.fisheye.rep.RepositoryHandle;

public class FisheyePluginSettingsFactory implements PluginSettingsFactory
{
    public PluginSettings createSettingsForKey(String key)
    {
        PropertiesType props;
        final RootConfig rootConfig = AppConfig.getsConfig();

        if (key == null || key.trim().length()==0)
        {
            // get the global config
            props = rootConfig.getConfig().getProperties();
            if (props==null)
            {
                props = rootConfig.getConfig().addNewProperties();
            }
        } else
        {
            // get per-repository config
            final RepositoryHandle handle = rootConfig.getRepositoryManager().getRepository(key);
            if (handle != null)
            {
                final RepositoryType repositoryTypeConfig = handle.getCfg().getRepositoryTypeConfig();
                props = repositoryTypeConfig.getProperties();
                if (props==null)
                {
                    props = repositoryTypeConfig.addNewProperties();
                }
            } else
            {
                throw new IllegalArgumentException("Repository handle for repository '"+key+"' not found.");
            }
        }
        return new FisheyePluginSettings(rootConfig, props);
    }

    public PluginSettings createGlobalSettings()
    {
        return createSettingsForKey(null);
    }

	public PluginSettings createUserSettings(String username)
	{
		throw new UnsupportedOperationException("Not yet implemented in FishEye.");
	}
}
