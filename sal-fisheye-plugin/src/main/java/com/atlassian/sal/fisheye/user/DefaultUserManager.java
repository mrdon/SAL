package com.atlassian.sal.fisheye.user;

import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.User;
import com.atlassian.sal.fisheye.Plugins2Hacks;
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
    private final PluginSettingsFactory pluginSettingsFactory;
    private com.cenqua.fisheye.user.UserManager userManager;

    DefaultUserManager(final PluginSettingsFactory pluginSettingsfactory)
    {
        this.pluginSettingsFactory = pluginSettingsfactory;
    }

    public String getRemoteUsername()
    {
        return Plugins2Hacks.doInApplicationContext(new Callable<String>()
        {
            public String call() throws Exception
            {
                return _getRemoteUsername();
            }
        });
    }
    public String _getRemoteUsername()
    {
        try
        {
            final UserLogin user = getUserManager().getCurrentUser(CrucibleFilter.getRequest());
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

    public boolean isSystemAdmin(final String username)
    {
        return Plugins2Hacks.doInApplicationContext(new Callable<Boolean>()
        {

            public Boolean call() throws Exception
            {
                return _isSystemAdmin(username);
            }
        });
    }
    public boolean _isSystemAdmin(final String username)
    {
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
        return Plugins2Hacks.doInApplicationContext(new Callable<Boolean>()
        {
            public Boolean call() throws Exception
            {
                return _isUserInGroup(username, group);
            }
        });
    }
    public boolean _isUserInGroup(final String username, final String group)
    {
        try
        {
            return getUserManager().isUserInGroup(group, username);
        }
        catch (final DbException e)
        {
            log.error("Database error trying to test users group: '" + username + "'", e);
        }
        return false;
    }

    public boolean authenticate(final String username, final String password)
    {
        return Plugins2Hacks.doInApplicationContext(new Callable<Boolean>()
        {
            public Boolean call() throws Exception
            {
                return _authenticate(username, password);
            }
        });
    }

    public User getUser(String username)
    {
        throw new UnsupportedOperationException();
    }

    public User createUser(User user)
    {
        throw new UnsupportedOperationException();
    }

    public User updateUser(User user)
    {
        throw new UnsupportedOperationException();
    }

    public void removeUser(String username)
    {
        throw new UnsupportedOperationException();
    }

    public boolean _authenticate(final String username, final String password)
    {
        try
        {
            final UserLogin userLogin = getUserManager().login(CrucibleFilter.getRequest(), CrucibleFilter.getResponse(), username, password, false);
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

    private com.cenqua.fisheye.user.UserManager getUserManager()
    {
        if (userManager==null)
        {
            userManager = AppConfig.getsConfig().getUserManager();
        }
        return userManager;
    }

    public String getRemoteUsername(final HttpServletRequest request)
    {
        // TODO Implement SAL-16
        return getRemoteUsername();
    }

}
