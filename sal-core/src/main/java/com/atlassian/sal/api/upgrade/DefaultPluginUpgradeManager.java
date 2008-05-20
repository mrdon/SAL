package com.atlassian.sal.api.upgrade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginManager;
import com.atlassian.sal.api.component.ComponentLocator;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;

/**
 * Processes plugin upgrade operations.  Upgrades are triggered by the start lifecycle event.
 */
public class DefaultPluginUpgradeManager implements PluginUpgradeManager, LifecycleAware
{
	private static final Logger log = Logger.getLogger(DefaultPluginUpgradeManager.class);

    /**
	 * @return map of all upgrade tasks (stored by pluginKey)
	 */
	private Map<String, List<PluginUpgradeTask>> getUpgradeTasks()
	{
		Map<String, List<PluginUpgradeTask>> pluginUpgrades = new HashMap<String, List<PluginUpgradeTask>>();
		
		// Find all implementations of PluginUpgradeTask
		Collection<PluginUpgradeTask> implementations = ComponentLocator.getComponents(PluginUpgradeTask.class);
    	for (PluginUpgradeTask upgradeTask : implementations)
		{
    		List<PluginUpgradeTask> upgrades = pluginUpgrades.get(upgradeTask.getPluginKey());
    		if (upgrades==null)
    		{
    			upgrades=new ArrayList<PluginUpgradeTask>();
    			pluginUpgrades.put(upgradeTask.getPluginKey(), upgrades);
    		}
    		upgrades.add(upgradeTask);
		}
    	
    	return pluginUpgrades;
	}
	
	public List<Message> upgrade()
	{
        log.info("Running plugin upgrade tasks...");

		// 1. get all upgrade tasks for all plugins
		Map<String, List<PluginUpgradeTask>> pluginUpgrades = getUpgradeTasks();
		

		PluginManager pluginManager = ComponentLocator.getComponent(PluginManager.class);
		PluginSettingsFactory pluginSettingsFactory = ComponentLocator.getComponent(PluginSettingsFactory.class);

		ArrayList<Message> messages = new ArrayList<Message>();

		// 2. for each plugin, sort tasks by build number and execute them
		for (String pluginKey : pluginUpgrades.keySet())
		{
			List<PluginUpgradeTask> upgrades = pluginUpgrades.get(pluginKey);

			Plugin plugin = pluginManager.getPlugin(pluginKey);
			if (plugin == null)
				throw new IllegalArgumentException("Invalid plugin key: " + pluginKey);

			PluginUpgrader pluginUpgrader = new PluginUpgrader(plugin, pluginSettingsFactory.createGlobalSettings(), upgrades);
			List<Message> upgradeMessages = pluginUpgrader.upgrade();
			if (upgradeMessages != null)
			{
				messages.addAll(upgradeMessages);
			}
		}

		return messages;
	}

    public void onStart()
    {
        //JRA-737: Need to ensure upgrades run in a transaction.  Just calling upgrade here may not provide this
        //as no this may be executed outside of a 'normal' context where a transaction is available.
        TransactionTemplate txTemplate = ComponentLocator.getComponent(TransactionTemplate.class);
        List<Message> messages = (List<Message>) txTemplate.execute(new TransactionCallback()
        {
            public Object doInTransaction()
            {
                return upgrade();
            }
        });

        // TODO 1: should do something useful with the messages
    	// TODO 2: we don't know what upgrade tasks these messages came from
        for(Message msg : messages)
        {
            log.error("Upgrade error: "+msg);
        }
    }

}
