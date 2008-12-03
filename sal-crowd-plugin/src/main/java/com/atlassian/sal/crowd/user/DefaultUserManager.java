package com.atlassian.sal.crowd.user;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.anonymous.AnonymousAuthenticationToken;

import com.atlassian.crowd.integration.acegi.user.CrowdUserDetails;
import com.atlassian.sal.api.user.UserManager;

/**
 * FishEye implementation of the UserManager
 */
public class DefaultUserManager implements UserManager
{
    public String getRemoteUsername()
    {
    	final CrowdUserDetails user = getUser();
    	if (user == null)
			return null;

    	return user.getUsername();
    }

    public boolean isSystemAdmin(String username)
    {
    	final CrowdUserDetails user = getUser();

    	if (user == null)
    		return false;

        for (int i = 0; i < user.getAuthorities().length; i++)
        {
            if (user.getAuthorities()[i].getAuthority().equals("ROLE_ADMIN"))
                return true;
        }
    	
        return false;
    }

    public boolean isUserInGroup(String username, String group)
    {
    	throw new UnsupportedOperationException();
    }

    public boolean authenticate(String username, String password)
    {
    	throw new UnsupportedOperationException();
    }
	
	private CrowdUserDetails getUser()
	{
		final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && !(auth instanceof AnonymousAuthenticationToken) && auth.getPrincipal() != null && (auth.getPrincipal() instanceof CrowdUserDetails))
		{
			return (CrowdUserDetails) auth.getPrincipal();
		}
		
		return null;
	}
}
