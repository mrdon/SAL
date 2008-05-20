package com.atlassian.sal.api.upgrade;

import java.util.Collection;
import java.util.List;

import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.upgrade.PluginUpgradeTask;

public class UpgradeTaskStub implements PluginUpgradeTask
{
    private int buildNumber;
    private List<Message> errors;
    private boolean upgraded = false;


    public UpgradeTaskStub(int buildNumber)
    {
        this.buildNumber = buildNumber;
    }

    public UpgradeTaskStub(int buildNumber, List<Message> errors)
    {
        this.buildNumber = buildNumber;
        this.errors = errors;
    }

    public int getBuildNumber()
    {
        return buildNumber;
    }

    public String getShortDescription()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Collection<Message> doUpgrade() throws Exception
    {
        upgraded = true;
        return errors;
    }

    public void setErrors(List<Message> errors)
    {
        this.errors = errors;
    }

    public void setBuildNumber(int buildNumber)
    {
        this.buildNumber = buildNumber;
    }

    public boolean isUpgraded()
    {
        return upgraded;
    }

	public String getPluginKey()
	{
		return null;
	}
}
