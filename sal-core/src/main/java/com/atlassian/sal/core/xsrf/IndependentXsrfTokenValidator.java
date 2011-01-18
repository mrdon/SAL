package com.atlassian.sal.core.xsrf;

import com.atlassian.sal.api.xsrf.XsrfTokenAccessor;
import com.atlassian.sal.api.xsrf.XsrfTokenValidator;

import javax.servlet.http.HttpServletRequest;

/**
 * XSRF token validator that manages its own tokens, not using the underlying applications XSRF tokens
 *
 * @since 2.4
 */
public class IndependentXsrfTokenValidator implements XsrfTokenValidator
{
    public static final String XSRF_PARAM_NAME = "atl_token";

    private XsrfTokenAccessor accessor;

    public IndependentXsrfTokenValidator(XsrfTokenAccessor accessor)
    {
        this.accessor = accessor;
    }

    public boolean validateFormEncodedToken(HttpServletRequest request)
    {
        String parameterToken = request.getParameter(XSRF_PARAM_NAME);
        String requestToken = accessor.getXsrfToken(request, null, false);

        return parameterToken != null && parameterToken.equals(requestToken);
    }

    public String getXsrfParameterName()
    {
        return XSRF_PARAM_NAME;
    }
}
