package com.atlassian.sal.core.websudo;

import com.atlassian.sal.api.websudo.WebSudoManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * NO OP implementation of the {@link com.atlassian.sal.api.websudo.WebSudoManager} that can be used
 * if the host application does not support WebSudo.
 */
public class NoopWebSudoManager implements WebSudoManager
{
    public boolean isWebSudoProtected(final HttpServletRequest request)
    {
        return true; // Code protected by WebSudo should still be executed if the host application doesn't support WebSudo
    }

    public void enforceWebSudoProtection(final HttpServletRequest request, final HttpServletResponse response)
    {
        // NO OP
    }
}
