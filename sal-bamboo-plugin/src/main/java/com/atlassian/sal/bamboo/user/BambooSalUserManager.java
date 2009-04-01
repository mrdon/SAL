package com.atlassian.sal.bamboo.user;

import org.apache.log4j.Logger;
import org.acegisecurity.Authentication;
import org.acegisecurity.adapters.PrincipalAcegiUserToken;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.context.SecurityContextHolder;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserResolutionException;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.bamboo.user.BambooUserManager;
import com.atlassian.bamboo.user.BambooUser;
import com.atlassian.bamboo.security.BambooPermissionManager;
import com.atlassian.bamboo.security.GlobalApplicationSecureObject;
import com.atlassian.user.User;
import com.atlassian.seraph.config.SecurityConfig;
import com.atlassian.seraph.config.SecurityConfigFactory;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

public class BambooSalUserManager implements UserManager
{
    private static final Logger log = Logger.getLogger(BambooSalUserManager.class);
    // ------------------------------------------------------------------------------------------------------- Constants
    public static final String USER_TOKEN_KEY = "USER_TOKEN_KEY";

    // ------------------------------------------------------------------------------------------------- Type Properties
    // ---------------------------------------------------------------------------------------------------- Dependencies
    private BambooUserManager bambooUserManager;
    private BambooPermissionManager bambooPermissionManager;
    private TransactionTemplate transactionTemplate;

    // ---------------------------------------------------------------------------------------------------- Constructors

    public BambooSalUserManager(BambooUserManager bambooUserManager, BambooPermissionManager bambooPermissionManager, TransactionTemplate transactionTemplate)
    {
        this.bambooUserManager = bambooUserManager;
        this.bambooPermissionManager = bambooPermissionManager;
        this.transactionTemplate = transactionTemplate;
    }
    // ----------------------------------------------------------------------------------------------- Interface Methods
    // -------------------------------------------------------------------------------------------------- Action Methods
    // -------------------------------------------------------------------------------------------------- Public Methods

    public String getRemoteUsername()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null)
        {
            authentication.getName();
        }
        return null;
    }

    public String getRemoteUsername(HttpServletRequest httpServletRequest)
    {
        SecurityConfig securityConfig = SecurityConfigFactory.getInstance(null);

        if (httpServletRequest != null)
        {
            return securityConfig.getAuthenticator().getRemoteUser(httpServletRequest);
        }
        return null;
    }

    
    public boolean isUserInGroup(String username, String group)
    {
        return bambooUserManager.hasMembership(group, username);
    }

    public boolean isSystemAdmin(String username)
    {
        final UserDetails userDetails = bambooUserManager.loadUserByUsername(username);
        if (userDetails != null)
        {
            final Authentication auth = (Authentication) transactionTemplate.execute(new TransactionCallback()
            {
                public Object doInTransaction()
                {
                    return new PrincipalAcegiUserToken(USER_TOKEN_KEY, userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities(), userDetails);

                }
            });

            return bambooPermissionManager.hasPermission(auth, "ADMINISTRATION", GlobalApplicationSecureObject.INSTANCE);
        }
        return false;
    }

    public Principal resolve(String username) throws UserResolutionException
    {
        return bambooUserManager.getBambooUser(username);
    }

    public boolean authenticate(String username, String password)
    {
        final User user = bambooUserManager.getUser(username);
        return user != null && bambooUserManager.authenticate(user.getName(), password);
    }
    // ------------------------------------------------------------------------------------------------- Helper Methods
    // -------------------------------------------------------------------------------------- Basic Accessors / Mutators
}
