package com.atlassian.sal.fisheye.appconfig;

import javax.servlet.http.HttpServletRequest;

public interface FisheyeUserManagerAccessor
{

    String getRemoteUsername();

    boolean isSystemAdmin(String username);

    boolean isUserInGroup(String username, String groupname);

    boolean authenticate(String username, String password);

    String getRemoteUsername(HttpServletRequest request);

}
