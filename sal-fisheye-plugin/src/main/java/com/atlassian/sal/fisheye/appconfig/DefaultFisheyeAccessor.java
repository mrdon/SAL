package com.atlassian.sal.fisheye.appconfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.cenqua.fisheye.AppConfig;
import com.cenqua.fisheye.config.AdminConfig;
import com.cenqua.fisheye.config.ConfigException;
import com.cenqua.fisheye.config.RepositoryManager;
import com.cenqua.fisheye.config.RootConfig;
import com.cenqua.fisheye.config1.ConfigDocument;
import com.cenqua.fisheye.config1.LicenseType;
import com.cenqua.fisheye.config1.LinkerSimpleType;
import com.cenqua.fisheye.config1.LinkerType;
import com.cenqua.fisheye.config1.RepSecurityType;
import com.cenqua.fisheye.config1.RepositoryType;
import com.cenqua.fisheye.config1.SvnRepType;
import com.cenqua.fisheye.license.LicenseException;
import com.cenqua.fisheye.rep.DbException;
import com.cenqua.fisheye.rep.RepositoryHandle;
import com.cenqua.fisheye.user.AdminUserConfig;
import com.cenqua.fisheye.util.XmlbeansUtil;
import com.cenqua.fisheye.web.admin.actions.svn.SvnSymbolicHelper;
import com.cenqua.crucible.actions.admin.project.ProjectData;
import com.cenqua.crucible.actions.admin.project.ProjectDataFactory;
import com.cenqua.crucible.model.managers.ProjectManager;
import com.cenqua.crucible.model.Project;
import com.atlassian.crucible.spi.TxTemplate;
import com.atlassian.crucible.spi.TxCallback;
import org.springframework.transaction.TransactionStatus;

public class DefaultFisheyeAccessor implements FisheyeAccessor
{
    private static final int REPO_CHILD_INDENT = 6;
    private static final int REPO_INDENT = 2;

    private final ProjectManager projectManager;
    private final ProjectDataFactory projectDataFactory;
    private final TxTemplate txTemplate;

    public DefaultFisheyeAccessor(ProjectManager projectManager, ProjectDataFactory projectDataFactory,
        TxTemplate txTemplate)
    {
        this.projectManager = projectManager;
        this.projectDataFactory = projectDataFactory;
        this.txTemplate = txTemplate;
    }

    public boolean repositoryExists(final String repName)
    {
        final RootConfig rootConfig = AppConfig.getsConfig();
        final RepositoryHandle handle = rootConfig.getRepositoryManager().getRepository(repName);
        return handle != null;
    }

    public String getSiteURL()
    {
        return AppConfig.getsConfig().getSiteURL();
    }

    public void createRepository(final String repName, final String description, final String svnUrl,
        final String svnUsername, final String svnPassword, final List<Linker> linkers) throws FisheyeAccessorException
    {
        try
        {
            // Setup SVN details
            final SvnSymbolicHelper svnSymbolic = new SvnSymbolicHelper();
            svnSymbolic.setupType1();
            final SvnRepType svn = SvnRepType.Factory.newInstance();
            svn.setUrl(svnUrl);
            svn.setPath(repName);
            svn.setSymbolic(svnSymbolic.toXmlBean());
            svn.addNewAuth();
            svn.getAuth().setUsername(svnUsername);
            svn.getAuth().setPassword(svnPassword);
            svn.setCharset("UTF-8");

            RepositoryType repository = RepositoryType.Factory.newInstance();
            repository.setName(repName);
            final int rep = AppConfig.getsConfig().addRepositoryType(repository);
            repository = AppConfig.getsConfig().getRepositoryType(rep);
            repository.setDescription(description);
            repository.setSvn(svn);
            repository.setEnabled(true);
            XmlbeansUtil.placeOnNewLine(repository.getSvn(), REPO_CHILD_INDENT);
            XmlbeansUtil.placeOnNewLine(repository, REPO_INDENT);

            // set up linkers
            final LinkerType linkerType = LinkerType.Factory.newInstance();
            for (final Linker linker : linkers)
            {
                final LinkerSimpleType simpleLinkerConfig = linkerType.addNewSimple();
                simpleLinkerConfig.setRegex(linker.getRegexp());
                simpleLinkerConfig.setHref(linker.getHref());
                simpleLinkerConfig.setDescription(linker.getDescription());
            }
            repository.setLinker(linkerType);

            // save the config
            AppConfig.getsConfig().saveConfig();
            // refresh config and start the repository
            final RepositoryManager rm = AppConfig.getsConfig().getRepositoryManager();
            rm.reloadList();
            rm.runRepository(repName);
        }
        catch (final Exception e)
        {
            throw new FisheyeAccessorException("Error creating fisheye repository: " + e.getMessage(), e);
        }
    }

    public void deleteRepository(final String repName) throws FisheyeAccessorException
    {
        AppConfig.getsConfig().getRepositoryManager().getRepository(repName).stop();

        final int id = getRepositoryId(repName);
        try
        {
            AppConfig.getsConfig().getConfig().removeRepository(id);
            AppConfig.getsConfig().saveConfig();
            AppConfig.getsConfig().getRepositoryManager().reloadList();
        }
        catch (final IndexOutOfBoundsException e)
        {
            throw new FisheyeAccessorException(
                "Error retrieving repository '" + repName + "' for id '" + id + "'. Unable to delete.", e);
        }
        catch (final IOException e)
        {
            throw new FisheyeAccessorException(
                "Error saving fisheye config when deleting repository '" + repName + "'.");
        }
        catch (final ConfigException e)
        {
            throw new FisheyeAccessorException(
                "Error reloading fisheye config when deleting repository '" + repName + "'.");
        }
    }

    public void setRepositoryLinkers(final String repositoryName, final List<Linker> linkers)
        throws FisheyeAccessorException
    {
        try
        {
            // find RepositoryType for given repositoryName
            final RepositoryType repositoryType = getRepositoryType(repositoryName);
            if (repositoryType == null)
            {
                throw new FisheyeAccessorException(
                    "Error setting up repository linkers. Repository " + repositoryName + " not found");
            }

            // add all default linkers
            final LinkerType linkerType = LinkerType.Factory.newInstance();
            for (final Linker linker : linkers)
            {
                final LinkerSimpleType simpleLinkerConfig = linkerType.addNewSimple();
                simpleLinkerConfig.setRegex(linker.getRegexp());
                simpleLinkerConfig.setHref(linker.getHref());
                simpleLinkerConfig.setDescription(linker.getDescription());
            }
            repositoryType.setLinker(linkerType);

            // some extra magic for linkers to work
            final RepositoryHandle repositoryHandle = AppConfig.getsConfig().getRepositoryManager().getRepository(
                repositoryName);
            repositoryHandle.getCfg().setupLinker();

            // save the config
            AppConfig.getsConfig().saveConfig();

            // refresh config
            final RepositoryManager rm = AppConfig.getsConfig().getRepositoryManager();
            rm.reloadList();
        }
        catch (final IOException e)
        {
            throw new FisheyeAccessorException("Error setting up repository linkers: " + e, e);
        }
        catch (final ConfigException e)
        {
            throw new FisheyeAccessorException("Error setting up repository linkers: " + e, e);
        }
    }

    private int getRepositoryId(final String repositoryName)
    {
        final RepositoryType[] repositoryArray = AppConfig.getsConfig().getConfig().getRepositoryArray();
        for (int i = 0; i < repositoryArray.length; i++)
        {
            final RepositoryType repositoryType = repositoryArray[i];
            if (repositoryName.equals(repositoryType.getName()))
            {
                return i;
            }
        }
        return -1;
    }

    private RepositoryType getRepositoryType(final String repositoryName)
    {
        final RepositoryType[] repositories =
            AppConfig.getsConfig().getConfigDocument().getConfig().getRepositoryArray();
        for (final RepositoryType repository : repositories)
        {
            if (repository.getName().equals(repositoryName))
            {
                return repository;
            }
        }
        return null;
    }

    public void enableAnonymousAccess(final boolean enable) throws FisheyeAccessorException
    {
        try
        {
            final ConfigDocument.Config cfg = AppConfig.getsConfig().getConfig();
            final RepSecurityType security = cfg.getRepositoryDefaults().getSecurity();
            security.setAllowAnon(enable);
            AppConfig.getsConfig().saveConfig();
        }
        catch (final IOException e)
        {
            throw new FisheyeAccessorException(
                "Error " + (enable ? "enabling" : "disabling") + " anonymous access to fisheye repositories. " + e, e);
        }
    }

    public void setLicense(final String license) throws FisheyeAccessorException
    {
        final RootConfig rootConfig = AppConfig.getsConfig();
        final LicenseType licenses = AppConfig.getsConfig().getConfig().getLicense();
        licenses.setCrucible(license);
        licenses.setFisheye(license);
        try
        {
            rootConfig.saveConfig();
            rootConfig.refreshLicenses();
        }
        catch (final IOException ioe)
        {
            throw new FisheyeAccessorException("Error saving configuration while reloading license", ioe);
        }
        catch (final LicenseException le)
        {
            throw new FisheyeAccessorException("Error loading license", le);
        }
    }

    public boolean isApplicationSetUp()
    {
        // this code is copied from TotalityFilter.requresSetup() method
        final RootConfig rootConfig = AppConfig.getsConfig();
        final AdminConfig acfg = rootConfig.getAdminConfig();
        final boolean requiresSetup =
            !acfg.haveDoneInitialSetup() || (rootConfig.getLicense() == null) || rootConfig.getLicense().isTerminated();
        return !requiresSetup;
    }

    public Collection<String> getRepositoryNames()
    {
        // Get FishEye projects
        final Collection<String> results = new ArrayList<String>();
        final List<RepositoryHandle> handles = AppConfig.getsConfig().getRepositoryManager().getHandles();
        for (final RepositoryHandle handle : handles)
        {
            results.add(handle.getName());
        }
        return results;
    }

    public void updateProject(final ProjectData projectData)
    {
        txTemplate.execute(new TxCallback()
        {
            public Object doInTransaction(TransactionStatus transactionStatus) throws Exception
            {
                projectDataFactory.updateProject(projectManager, projectData);
                return null;
            }
        });
    }

    public ProjectData getProjectByKey(String key)
    {
        Project project = projectManager.getProjectByKey(key);
        if (project == null)
        {
            return null;
        }

        return new ProjectData(project);
    }

    public void addSysadminGroup(final String groupname) throws FisheyeAccessorException
    {
        try
        {
            final AdminUserConfig adminUserManager = AppConfig.getsConfig().getAdminUserManager();
            adminUserManager.addGroup(groupname);
            AppConfig.getsConfig().saveConfig();
        }
        catch (final IOException e)
        {
            throw new FisheyeAccessorException("IOException occured while trying to add sysadmin group: " + groupname,
                e);
        }
    }

    public Collection<String> getUsersInGroup(final String groupname) throws FisheyeAccessorException
    {
        try
        {
            return AppConfig.getsConfig().getUserManager().getUsersInGroup(groupname);
        }
        catch (final DbException e)
        {
            throw new FisheyeAccessorException("Exception occured while retrieving users for group: " + groupname, e);
        }
    }
}
