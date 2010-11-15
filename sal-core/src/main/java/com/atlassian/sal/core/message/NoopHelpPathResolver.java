package com.atlassian.sal.core.message;

import com.atlassian.sal.api.message.HelpPath;
import com.atlassian.sal.api.message.HelpPathResolver;

/**
 * Default noop implementation of HelpPathResolver.
 * This will return null in all cases.
 *
 * @since 2.4
 */
public class NoopHelpPathResolver implements HelpPathResolver
{
    public HelpPath getHelpPath(final String key)
    {
        return null;
    }
}
