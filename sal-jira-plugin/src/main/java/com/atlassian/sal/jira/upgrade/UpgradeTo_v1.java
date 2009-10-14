package com.atlassian.sal.jira.upgrade;

import com.atlassian.sal.api.upgrade.PluginUpgradeTask;
import com.atlassian.sal.api.message.Message;
import com.atlassian.core.ofbiz.CoreFactory;
import com.atlassian.core.util.map.EasyMap;
import com.atlassian.jira.propertyset.JiraPropertySetFactory;
import com.opensymphony.module.propertyset.PropertySet;

import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.ofbiz.core.entity.GenericValue;
import org.ofbiz.core.entity.GenericEntityException;
import org.apache.log4j.Logger;

/**
 * Convert Strings that were stored larger than 255 characters
 */
public class UpgradeTo_v1 implements PluginUpgradeTask
{
    private static final Logger log = Logger.getLogger(PluginUpgradeTask.class);
    private static final String MARKER = "#-#-#";
    private static final Pattern MARKER_PATTERN = Pattern.compile(MARKER + "([\\d]+)");

    private final JiraPropertySetFactory jiraPropertySetFactory;

    public UpgradeTo_v1(JiraPropertySetFactory jiraPropertySetFactory)
    {
        this.jiraPropertySetFactory = jiraPropertySetFactory;
    }

    public int getBuildNumber()
    {
        return 1;
    }

    public String getShortDescription()
    {
        return "Convert Strings that were split because they were larger than 255 characters to one row.";
    }

    @SuppressWarnings({"unchecked"})
    public Collection<Message> doUpgrade() throws Exception
    {
        log.info("Loading all string properties with values like #-#-#%, this may take some time...");
        // Find all the properties with a value like #-#-#%
        List<GenericValue> properties = getSplitProperties();
        log.info("Done. Converting " + properties.size() + " properties...");
        for (GenericValue gv : properties)
        {
            // Check that it is a valid SAL split field
            String value = gv.getString("value");
            Matcher matcher = MARKER_PATTERN.matcher(value);
            if (matcher.matches())
            {
                int parts = Integer.parseInt(matcher.group(1));
                long id = gv.getLong("id");
                // Get the entry
                GenericValue entry = getPropertyEntry(id);
                if (entry != null)
                {
                    String entityName = entry.getString("entityName");
                    long entityId = entry.getLong("entityId");
                    String key = entry.getString("propertyKey");

                    PropertySet propertySet = jiraPropertySetFactory.buildNoncachingPropertySet(entityName, entityId);
                    // Get the value
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < parts; i++)
                    {
                        String partKey = key + MARKER + i;
                        String propertyValue = propertySet.getString(partKey);
                        if (propertyValue != null)
                        {
                            sb.append(propertyValue);
                            // Remove the part
                            propertySet.remove(partKey);
                        }
                    }
                    // Remove the original key
                    propertySet.remove(key);
                    // Store the new key
                    propertySet.setText(key, sb.toString());
                }
            }
        }
        log.info("Conversion complete.");
        return null;
    }

    protected GenericValue getPropertyEntry(long id)
        throws GenericEntityException
    {
        return CoreFactory.getGenericDelegator().findByPrimaryKey("OSPropertyEntry", EasyMap.build("id", id));
    }

    @SuppressWarnings({"unchecked"})
    protected List<GenericValue> getSplitProperties()
        throws GenericEntityException
    {
        return CoreFactory.getGenericDelegator().findByLike("OSPropertyString", EasyMap.build("value", "#-#-#%"));
    }

    public String getPluginKey()
    {
        return JiraPluginUpgradeManager.SAL_PLUGIN_KEY;
    }
}
