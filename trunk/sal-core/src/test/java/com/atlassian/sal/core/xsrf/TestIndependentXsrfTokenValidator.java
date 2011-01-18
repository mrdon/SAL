package com.atlassian.sal.core.xsrf;

import com.atlassian.sal.api.xsrf.XsrfTokenAccessor;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestIndependentXsrfTokenValidator extends TestCase
{
    private IndependentXsrfTokenValidator validator;

    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private XsrfTokenAccessor mockAccessor;

    @Before
    public void setUp()
    {
        mockAccessor = mock(XsrfTokenAccessor.class);
        mockRequest = mock(HttpServletRequest.class);
        validator = new IndependentXsrfTokenValidator(mockAccessor);
    }

    @After
    public void tearDown()
    {
        validator = null;
        mockRequest = null;
        mockAccessor = null;
    }

    @Test
    public void testValidToken()
    {
        when(mockAccessor.getXsrfToken(mockRequest, null, false)).thenReturn("cookievalue");
        when(mockRequest.getParameter(validator.getXsrfParameterName())).thenReturn("cookievalue");

        assertTrue(validator.validateFormEncodedToken(mockRequest));
    }

    @Test
    public void testInvalidToken()
    {
        when(mockAccessor.getXsrfToken(mockRequest, null, false)).thenReturn("cookievalue");
        when(mockRequest.getParameter(validator.getXsrfParameterName())).thenReturn("somethingelse");

        assertFalse(validator.validateFormEncodedToken(mockRequest));
    }

    @Test
    public void testNoXsrfParameter()
    {
        when(mockAccessor.getXsrfToken(mockRequest, null, false)).thenReturn("cookievalue");

        assertFalse(validator.validateFormEncodedToken(mockRequest));
    }

    @Test
    public void testNoXsrfCookie()
    {
        when(mockRequest.getParameter(validator.getXsrfParameterName())).thenReturn("cookievalue");

        assertFalse(validator.validateFormEncodedToken(mockRequest));
    }

    @Test
    public void testNoXsrfAtAll()
    {
        assertFalse(validator.validateFormEncodedToken(mockRequest));
    }
}
