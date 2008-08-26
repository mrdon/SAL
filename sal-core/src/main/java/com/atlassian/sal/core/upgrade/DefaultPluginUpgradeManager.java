package com.atlassian.sal.core.upgrade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginManager;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.upgrade.PluginUpgradeTask;
import com.atlassian.sal.api.upgrade.PluginUpgradeManager;

/**
 * Processes plugin upgrade operations.  Upgrades are triggered by the start lifecycle event.
 */
public class DefaultPluginUpgradeManager implements PluginUpgradeManager, LifecycleAware
{
	private static final Logger log = Logger.getLogger(DefaultPluginUpgradeManager.class);

    private final List<PluginUpgradeTask> upgradeTasks;
    private final TransactionTemplate transactionTemplate;
    private final PluginAccessor pluginAccessor;
    private final PluginSettingsFactory pluginSettingsFactory;

    public DefaultPluginUpgradeManager(List<PluginUpgradeTask> upgradeTasks, TransactionTemplate transactionTemplate,
                                       PluginAccessor pluginAccessor, PluginSettingsFactory pluginSettingsFactory)
    {
        this.upgradeTasks = upgradeTasks;
        this.transactionTemplate = transactionTemplate;
        this.pluginAccessor = pluginAccessor;
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    /**
	 * @return map of all upgrade tasks (stored by pluginKey)
	 */
	private Map<String, List<PluginUpgradeTask>> getUpgradeTasks()
	{
		Map<String, List<PluginUpgradeTask>> pluginUpgrades = new HashMap<String, List<PluginUpgradeTask>>();
		
		// Find all implementations of PluginUpgradeTask
    	for (PluginUpgradeTask upgradeTask : upgradeTasks)
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

    public void onBind(PluginUpgradeTask task, Map props)
    {
        upgrade();
    }


    public List<Message> upgrade()
	{
		//JRA-737: Need to ensure upgrades run in a transaction.  Just calling upgrade here may not provide this
        //as no this may be executed outside of a 'normal' context where a transaction is available.
        List<Message> messages = (List<Message>) transactionTemplate.execute(new TransactionCallback()
        {
            public Object doInTransaction()
            {
                return upgradeInternal();
            }
        });
		return messages;
	}
	
	public List<Message> upgradeInternal()
	{
        log.info("Running plugin upgrade tasks...");

		// 1. get all upgrade tasks for all plugins
		Map<String, List<PluginUpgradeTask>> pluginUpgrades = getUpgradeTasks();
		

		ArrayList<Message> messages = new ArrayList<Message>();

		// 2. for each plugin, sort tasks by build number and execute them
		for (String pluginKey : pluginUpgrades.keySet())
		{
			List<PluginUpgradeTask> upgrades = pluginUpgrades.get(pluginKey);

			Plugin plugin = pluginAccessor.getPlugin(pluginKey);
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
        List<Message> messages = upgrade();

        // TODO 1: should do something useful with the messages
    	// TODO 2: we don't know what upgrade tasks these messages came from
        if (messages != null)
        {
            for(Message msg : messages)
            {
                log.error("Upgrade error: "+msg);
            }
        }
    }


}
