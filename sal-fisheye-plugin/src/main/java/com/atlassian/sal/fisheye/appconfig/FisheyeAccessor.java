package com.atlassian.sal.fisheye.appconfig;

import java.util.List;

import com.atlassian.fisheye.event.CommitEvent;
import com.cenqua.fisheye.rep.ChangeSet;

public interface FisheyeAccessor
{
    ChangeSet getCommitChangeSet(final CommitEvent commitEvent);

    boolean repositoryExists(String repositoryName);

    void createRepository(String name, String description, String svnUrl, String svnUsername, String svnPassword, List<Linker> linkers) throws FisheyeAccessorException;

    interface Linker
    {
        String getRegexp();
        String getHref();
        String getDescription();
    }

    String getSiteURL();

    public class FisheyeAccessorException extends Exception
    {
        public FisheyeAccessorException()
        {
            super();
        }

        public FisheyeAccessorException(final String message, final Throwable cause)
        {
            super(message, cause);
        }

        public FisheyeAccessorException(final String message)
        {
            super(message);
        }

        public FisheyeAccessorException(final Throwable cause)
        {
            super(cause);
        }

    }

    void deleteRepository(String key) throws FisheyeAccessorException;
}
