package com.atlassian.sal.fisheye.user;

import javax.servlet.http.HttpServletRequest;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.fisheye.appconfig.FisheyeUserManagerAccessor;

/**
 * FishEye implementation of the UserManager
 */
public class DefaultUserManager implements UserManager
{
    private final PluginSettingsFactory pluginSettingsFactory;
    private final FisheyeUserManagerAccessor fisheyeUserManagerAccessor;

    DefaultUserManager(final PluginSettingsFactory pluginSettingsfactory, final FisheyeUserManagerAccessor fisheyeUserManagerAccessor)
    {
        this.pluginSettingsFactory = pluginSettingsfactory;
        this.fisheyeUserManagerAccessor = fisheyeUserManagerAccessor;
    }

    public String getRemoteUsername()
    {
        return fisheyeUserManagerAccessor.getRemoteUsername();
    }

    public boolean isSystemAdmin(final String username)
    {
        // TODO: replace with:
        // return fisheyeUserManagerAccessor.isSystemAdmin(username);

        if (username == null)
        {
            return false;
        }

        final PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
        final String sysadminGroups = (String) pluginSettings.get("sysadmin-groups");
        if (sysadminGroups == null)
        {
            return false;
        }
        for (final String sysadminGroup : sysadminGroups.split(","))
        {
            if (isUserInGroup(username, sysadminGroup))
            {
                return true;
            }
        }
        return false;
    }

    public boolean isUserInGroup(final String username, final String group)
    {
        return fisheyeUserManagerAccessor.isUserInGroup(username, group);
    }

    public boolean authenticate(final String username, final String password)
    {
        return fisheyeUserManagerAccessor.authenticate(username, password);
    }

   public String getRemoteUsername(final HttpServletRequest request)
   {
       return fisheyeUserManagerAccessor.getRemoteUsername(request);
    }

}
