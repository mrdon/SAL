package com.atlassian.sal.core.xsrf;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.Cookie;

import static com.atlassian.sal.core.xsrf.IndependentXsrfTokenAccessor.XSRF_COOKIE_KEY;
import static org.junit.Assert.*;

/**
 * @since 2.4
 */
public class TestIndependentXsrfTokenAccessor
{
    private IndependentXsrfTokenAccessor accessor;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @Before
    public void setUp()
    {
        accessor = new IndependentXsrfTokenAccessor();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    public void testGetExistingToken()
    {
        request.setCookies(new Cookie[] {new Cookie(XSRF_COOKIE_KEY, "cookievalue")});
        assertEquals("cookievalue", accessor.getXsrfToken(request, response, true));
        assertNull(response.getCookie(XSRF_COOKIE_KEY));
    }

    @Test
    public void testGetExistingTokenNoCreate()
    {
        request.setCookies(new Cookie[] {new Cookie(XSRF_COOKIE_KEY, "cookievalue")});
        assertEquals("cookievalue", accessor.getXsrfToken(request, response, false));
        assertNull(response.getCookie(XSRF_COOKIE_KEY));
    }

    @Test
    public void testCreateToken()
    {
        String token = accessor.getXsrfToken(request, response, true);
        assertNotNull(token);
        Cookie cookie = response.getCookie(XSRF_COOKIE_KEY);
        assertNotNull(cookie);
        assertEquals(token, cookie.getValue());
    }

    @Test
    public void testGetTokenNoCreate()
    {
        assertNull(accessor.getXsrfToken(request, response, false));
        assertNull(response.getCookie(XSRF_COOKIE_KEY));
    }
}
