package com.atlassian.sal.jira.message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.web.bean.I18nBean;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.elements.ResourceDescriptor;
import com.atlassian.sal.api.message.AbstractI18nResolver;

/**
 * A JIRA I18nResolver.  Uses the user's locale if a user is logged in or the default
 * locale if none can be found.
 */
public class JiraI18nResolver extends AbstractI18nResolver
{
	private static final Logger log = Logger.getLogger(JiraI18nResolver.class);
    private final JiraAuthenticationContext jiraAuthenticationContext;
    private final PluginAccessor pluginAccessor;
    private final List<String> pluginResources;

    public JiraI18nResolver(JiraAuthenticationContext jiraAuthenticationContext, PluginAccessor pluginAccessor)
    {
        this.jiraAuthenticationContext = jiraAuthenticationContext;
        this.pluginAccessor = pluginAccessor;
        this.pluginResources =  initPluginI18NResources();
    }

    private I18nBean getI18nBean()
    {
        return new I18nBean(jiraAuthenticationContext.getUser())
        {
            public List getI18nLocations()
            {
                return pluginResources;
            }
        };
    }

    public String resolveText(String key, Serializable[] arguments)
    {
        return getI18nBean().getText(key, arguments);
    }

    // Stolen from Confluence :)
    // load all i18n resources declared inside plugin descriptor files (that is, atlassian-plugin.xml)
    private List<String> initPluginI18NResources()
    {
		//lazy load this, as the list will never change.
    	List<String> list = new ArrayList<String>();
    	list.addAll(new I18nBean().getI18nLocations());
		// find all i18n resource descriptors
		Set<String> i18nResourceDescriptors = new HashSet<String>();
		for (Iterator i = pluginAccessor.getEnabledPlugins().iterator(); i.hasNext();)
		{
			Plugin plugin = (Plugin) i.next();
			try
			{
				// first locate i18n resources declared in the plugin scope
				for (Iterator j = plugin.getResourceDescriptors("i18n").iterator(); j.hasNext();)
				{
					ResourceDescriptor resourceDescriptor = (ResourceDescriptor) j.next();
					i18nResourceDescriptors.add(resourceDescriptor.getLocation());
				}

				// then locate i18n resources declared within plugin modules
				for (Iterator iterator = plugin.getModuleDescriptors().iterator(); iterator.hasNext();)
				{
					ModuleDescriptor moduleDescriptor = (ModuleDescriptor) iterator.next();
					for (Iterator iterator1 = moduleDescriptor.getResourceDescriptors("i18n").iterator(); iterator1
							.hasNext();)
					{
						ResourceDescriptor resourceDescriptor = (ResourceDescriptor) iterator1.next();
						i18nResourceDescriptors.add(resourceDescriptor.getLocation());
					}
				}
			} catch (Exception e)
			{
				log.error("Unable to load i18n resources for: " + plugin.getName() + "(" + plugin.getKey()
						+ ") " + e.toString(), e);
			}
		}

		list.addAll(i18nResourceDescriptors);
		return list;
    }
}
