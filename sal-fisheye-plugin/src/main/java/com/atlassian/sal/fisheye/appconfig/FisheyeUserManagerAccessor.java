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

}
