package com.atlassian.sal.fisheye.appconfig;

import javax.servlet.http.HttpServletRequest;

import com.cenqua.fisheye.rep.DbException;
import com.cenqua.fisheye.user.FEUser;

public interface FisheyeUserManagerAccessor
{

    String getRemoteUsername();

    boolean isSystemAdmin(String username);

    boolean isUserInGroup(String username, String groupname);

    boolean authenticate(String username, String password);

    String getRemoteUsername(HttpServletRequest request);

    FEUser getUser(String username) throws DbException;

    /**
     * This method will force FishEye to consider the given username logged-in
     * for the current request.
     * THINK CAREFULLY ABOUT CALLING THIS METHOD as (e.g.) no password checking is done.
     */
    void loginUserForThisRequest(String username, HttpServletRequest request);
}
