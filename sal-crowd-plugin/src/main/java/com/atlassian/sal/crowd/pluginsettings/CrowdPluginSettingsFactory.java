package com.atlassian.sal.crowd.pluginsettings;

import com.atlassian.crowd.model.salproperty.SALPropertyDAO;
import com.atlassian.sal.api.component.ComponentLocator;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

public class CrowdPluginSettingsFactory implements PluginSettingsFactory
{
    private SALPropertyDAO salPropertyDAO;

    public CrowdPluginSettingsFactory()
    {
    }

    public PluginSettings createGlobalSettings()
    {
        return new CrowdPluginSettings(null, getSalPropertyDao());
    }

    public PluginSettings createSettingsForKey(String key)
    {
        return new CrowdPluginSettings(key, getSalPropertyDao());
    }

	private SALPropertyDAO getSalPropertyDao() {
		if (this.salPropertyDAO == null)
			this.salPropertyDAO = ComponentLocator.getComponent(SALPropertyDAO.class);
		return this.salPropertyDAO;
	}
}
