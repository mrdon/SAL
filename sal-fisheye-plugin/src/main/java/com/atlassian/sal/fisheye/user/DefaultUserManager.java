package com.atlassian.sal.fisheye.user;

import org.apache.log4j.Logger;

import com.atlassian.sal.api.component.ComponentLocator;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.user.UserManager;
import com.cenqua.crucible.filters.CrucibleFilter;
import com.cenqua.fisheye.AppConfig;
import com.cenqua.fisheye.LicensePolicyException;
import com.cenqua.fisheye.rep.DbException;
import com.cenqua.fisheye.user.UserLogin;

/**
 * FishEye implementation of the UserManager
 */
public class DefaultUserManager implements UserManager
{
    private static final Logger log = Logger.getLogger(DefaultUserManager.class);
    private PluginSettingsFactory pluginSettingsFactory;

    DefaultUserManager(PluginSettingsFactory pluginSettingsfactory)
    {
        this.pluginSettingsFactory = pluginSettingsfactory;
    }

    public String getRemoteUsername()
    {
        try
        {
            UserLogin user = AppConfig.getsConfig().getUserManager().getCurrentUser(CrucibleFilter.getRequest());
            if (user != null)
            {
                return user.getUserName();
            }
        }
        catch (final IllegalStateException ise)
        {
            log.error("Illegal State Exception while trying to get the remote user's name: ",ise);
        }
        return null;
    }

    public boolean isSystemAdmin(String username)
    {
        if (username == null)
        {
            return false;
        }
        
        final PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
        String sysadminGroups = (String) pluginSettings.get("sysadmin-groups");
        if (sysadminGroups == null)
        {
            return false;
        }
        for (String sysadminGroup : sysadminGroups.split(","))
        {
            if (isUserInGroup(username, sysadminGroup))
            {
                return true;
            }
        }
        return false;
    }

    public boolean isUserInGroup(String username, String group)
    {
        final com.cenqua.fisheye.user.UserManager userManager = AppConfig.getsConfig().getUserManager();
        try
        {
            return userManager.isUserInGroup(group, username);
        }
        catch (final DbException e)
        {
            log.error("Database error trying to test users group: '" + username + "'", e);
        }
        return false;
    }

    public boolean authenticate(String username, String password)
    {
        final com.cenqua.fisheye.user.UserManager userManager = AppConfig.getsConfig().getUserManager();
        try
        {
            final UserLogin userLogin = userManager.login(CrucibleFilter.getRequest(), CrucibleFilter.getResponse(), username, password, false);
            if (userLogin != null)
            {
                return true;
            }
        }
        catch (final DbException e)
        {
            log.error("Database error trying to authenticate user '" + username + "'", e);
        }
        catch (final LicensePolicyException e)
        {
            log.error("License error trying to authenticate user '" + username + "'", e);
        }
        return false;
    }
}
