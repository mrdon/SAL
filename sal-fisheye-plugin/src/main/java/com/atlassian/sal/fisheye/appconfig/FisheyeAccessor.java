package com.atlassian.sal.fisheye.appconfig;

import com.cenqua.crucible.actions.admin.project.ProjectData;
import com.cenqua.crucible.configuration.metrics.XMLValidationException;

import java.util.Collection;
import java.util.List;
import java.io.File;

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
    File getInstanceDirectory();
    void setLicense(String license) throws FisheyeAccessorException;
    boolean isApplicationSetUp();
    void addSysadminGroup(String systemAdmins) throws FisheyeAccessorException;
    Collection<String> getUsersInGroup(String groupname) throws FisheyeAccessorException;

    /* Crucible project administration */
    // These can't use the SPI ProjectData objects because they don't contain the data that needs to be updated
    void updateProject(ProjectData projectData);
    ProjectData getProjectByKey(String key);
    int updateCrucibleMetrics(String xml) throws FisheyeAccessorException;
    String getCrucibleMetrics();

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
