package com.atlassian.sal.fisheye.appconfig;

import java.util.Collection;
import java.util.List;

public interface FisheyeAccessor
{
    /* Repositories */
    void createRepository(String name, String description, String svnUrl, String svnUsername, String svnPassword, List<Linker> linkers) throws FisheyeAccessorException;
    void deleteRepository(String key) throws FisheyeAccessorException;
    void setRepositoryLinkers(String repositoryName, List<Linker> linkers) throws FisheyeAccessorException;
    void enableAnonymousAccess(boolean enable) throws FisheyeAccessorException;
    boolean repositoryExists(String repositoryName);
    Collection<String> getRepositoryNames();

    /* Other stuff */
    String getSiteURL();
    void setLicense(String license) throws FisheyeAccessorException;
    boolean isApplicationSetUp();


    interface Linker
    {
        String getRegexp();
        String getHref();
        String getDescription();
    }

    /* Exceptions */
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





}
