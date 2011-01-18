package com.atlassian.sal.api.message;

/**
 * Default immutable implementation of HelpPath.
 *
 * @since 2.4
 */
public class DefaultHelpPath implements HelpPath
{
    private final String key;
    private final String url;
    private final String title;
    private final String alt;
    private final boolean local;

    public DefaultHelpPath(final String key, final String url, final String title, final String alt, final boolean local)
    {
        this.key = key;
        this.url = url;
        this.title = title;
        this.alt = alt;
        this.local = local;
    }

    public String getKey()
    {
        return key;
    }

    public String getUrl()
    {
        return url;
    }

    public String getTitle()
    {
        return title;
    }

    public String getAlt()
    {
        return alt;
    }

    public boolean isLocal()
    {
        return local;
    }
}
