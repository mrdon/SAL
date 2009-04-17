package com.atlassian.sal.fisheye.appconfig;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.cenqua.crucible.filters.CrucibleFilter;
import com.cenqua.fisheye.AppConfig;
import com.cenqua.fisheye.LicensePolicyException;
import com.cenqua.fisheye.rep.DbException;
import com.cenqua.fisheye.user.FEUser;
import com.cenqua.fisheye.user.UserLogin;
import com.cenqua.fisheye.user.UserManager;

public class DefaultFisheyeUserManagerAccessor implements FisheyeUserManagerAccessor
{
    private static final Logger log = Logger.getLogger(DefaultFisheyeUserManagerAccessor.class);

    public String getRemoteUsername()
    {
        return getRemoteUsername(CrucibleFilter.getRequest());
    }

    private com.cenqua.fisheye.user.UserManager getUserManager()
    {
        return AppConfig.getsConfig().getUserManager();
    }

    public boolean isSystemAdmin(final String username)
    {
        try
        {
            return getUserManager().hasSysAdminPrivileges(username);
        } catch (final DbException e)
        {
            log.error("Database error while checking user '" + username + "' for sysadmin permissions.", e);
        }
        return false;

    }

    public boolean isUserInGroup(final String username, final String groupname)
    {
        try
        {
            return getUserManager().isUserInGroup(groupname, username);
        }
        catch (final DbException e)
        {
            log.error("Database error trying to test users group: '" + username + "'", e);
        }
        return false;
    }


    public boolean authenticate(final String username, final String password)
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

    public String getRemoteUsername(final HttpServletRequest request)
    {
        try
        {
            final UserLogin user = getUserManager().getCurrentUser(request);
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

    public FEUser getUser(final String username) throws DbException
    {
        return getUserManager().getUser(username);
    }

    public void loginUserForThisRequest(String username, HttpServletRequest request) {
        try {
            UserLogin user = getUserManager().createTrustedUserLogin(username, true, false);
            request.setAttribute(UserManager.USER_ATTR_KEY, user);
        } catch (LicensePolicyException e) {
            log.error("License problem authenticating request", e);
        } catch (DbException e) {
            log.error("Problem authenticating request", e);
        }

    }
}
