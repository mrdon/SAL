package com.atlassian.sal.fisheye.user;

import org.apache.log4j.Logger;

import com.atlassian.sal.api.user.UserManager;
import com.cenqua.crucible.filters.CrucibleFilter;
import com.cenqua.fisheye.AppConfig;
import com.cenqua.fisheye.LicensePolicyException;
import com.cenqua.fisheye.rep.DbException;
import com.cenqua.fisheye.user.UserLogin;

/**
 */
public class DefaultUserManager implements UserManager
{
	private static final Logger log = Logger.getLogger(DefaultUserManager.class);

    public String getRemoteUsername()
    {
        UserLogin user = AppConfig.getsConfig().getUserManager().getCurrentUser(CrucibleFilter.getRequest());
        if(user != null)
        {
            return user.getUserName();
        }
        return null;        
    }

    public boolean isSystemAdmin(String username)
    {
        //TODO: FishEye doesn't have a concept of an admin permission.  Probably should do something like this though:
//        AdminConfig admin = AppConfig.getsConfig().getAdminConfig();
//        if (admin.verifyAdminPassword(password))
//        {
//            return true;
//        }
        return true;
    }

    public boolean authenticate(String username, String password)
    {
        final com.cenqua.fisheye.user.UserManager um = AppConfig.getsConfig().getUserManager();
        try
        {
            final UserLogin userLogin = um.login(CrucibleFilter.getRequest(), CrucibleFilter.getResponse(), username, password, false);
            if (userLogin != null)
            {
                return true;
            }
        }
        catch (DbException e)
        {
            log.error("Database error trying to authenticate user '" + username + "'", e);
        }
        catch (LicensePolicyException e)
        {
            log.error("License error trying to authenticate user '" + username + "'", e);
        }
        return false;
    }
}
