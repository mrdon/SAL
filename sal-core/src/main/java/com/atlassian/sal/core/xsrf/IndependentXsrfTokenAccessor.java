package com.atlassian.sal.core.xsrf;

import com.atlassian.sal.api.xsrf.XsrfTokenAccessor;
import com.atlassian.security.random.DefaultSecureTokenGenerator;
import com.atlassian.security.random.SecureTokenGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * XSRF token accessor that manages its own tokens, not using the underlying applications XSRF tokens
 *
 * @since v2.4
 */
public class IndependentXsrfTokenAccessor implements XsrfTokenAccessor
{
    private static final Logger log = LoggerFactory.getLogger(IndependentXsrfTokenAccessor.class);
    public static final String XSRF_COOKIE_KEY = "atl.xsrf.token";

    private final SecureTokenGenerator tokenGenerator = DefaultSecureTokenGenerator.getInstance();

    public String getXsrfToken(final HttpServletRequest request, final HttpServletResponse response, final boolean create)
    {
        Cookie[] cookies = request.getCookies();
        if (cookies != null)
        {
            for (Cookie cookie : request.getCookies())
            {
                if (cookie.getName().equals(XSRF_COOKIE_KEY))
                {
                    return cookie.getValue();
                }
            }
        }
        if (create)
        {
            if (response.isCommitted())
            {
                log.warn("Adding cookie to committed response, this will likely have no effect");
            }
            String token = tokenGenerator.generateToken();
            Cookie cookie = new Cookie(XSRF_COOKIE_KEY, token);
            response.addCookie(cookie);
            return token;
        }
        return null;
    }
}
