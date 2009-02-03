package com.atlassian.sal.fisheye.appconfig;

import java.io.IOException;
import java.util.List;

import com.cenqua.fisheye.AppConfig;
import com.cenqua.fisheye.config.ConfigException;
import com.cenqua.fisheye.config.RepositoryManager;
import com.cenqua.fisheye.config.RootConfig;
import com.cenqua.fisheye.config1.ConfigDocument;
import com.cenqua.fisheye.config1.LinkerSimpleType;
import com.cenqua.fisheye.config1.LinkerType;
import com.cenqua.fisheye.config1.RepSecurityType;
import com.cenqua.fisheye.config1.RepositoryType;
import com.cenqua.fisheye.config1.SvnRepType;
import com.cenqua.fisheye.rep.RepositoryHandle;
import com.cenqua.fisheye.util.XmlbeansUtil;
import com.cenqua.fisheye.web.admin.actions.svn.SvnSymbolicHelper;

public class DefaultFisheyeAccessor implements FisheyeAccessor
{
    private static final int REPO_CHILD_INDENT = 6;
    private static final int REPO_INDENT = 2;

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

    public void createRepository(final String repName, final String description, final String svnUrl, final String svnUsername, final String svnPassword, final List<Linker> linkers) throws FisheyeAccessorException
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
        } catch (final Exception e)
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
            throw new FisheyeAccessorException("Error retrieving repository '" + repName + "' for id '" + id + "'. Unable to delete.", e);
        }
        catch (final IOException e)
        {
            throw new FisheyeAccessorException("Error saving fisheye config when deleting repository '" + repName + "'.");
        }
        catch (final ConfigException e)
        {
            throw new FisheyeAccessorException("Error reloading fisheye config when deleting repository '" + repName + "'.");
        }
    }

    public void setRepositoryLinkers(final String repositoryName, final List<Linker> linkers) throws FisheyeAccessorException
    {
        try
        {
            // find RepositoryType for given repositoryName
            final RepositoryType repositoryType = getRepositoryType(repositoryName);
            if (repositoryType == null)
            {
                throw new FisheyeAccessorException("Error setting up repository linkers. Repository " + repositoryName + " not found");
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
            final RepositoryHandle repositoryHandle = AppConfig.getsConfig().getRepositoryManager().getRepository(repositoryName);
            repositoryHandle.getCfg().setupLinker();

            // save the config
            AppConfig.getsConfig().saveConfig();

            // refresh config
            final RepositoryManager rm = AppConfig.getsConfig().getRepositoryManager();
            rm.reloadList();
        } catch (final IOException e)
        {
            throw new FisheyeAccessorException("Error setting up repository linkers: " + e, e);
        } catch (final ConfigException e)
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
        final RepositoryType[] repositories = AppConfig.getsConfig().getConfigDocument().getConfig().getRepositoryArray();
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
        } catch (final IOException e)
        {
            throw new FisheyeAccessorException("Error " + (enable?"enabling":"disabling") +" anonymous access to fisheye repositories. " + e, e);
        }
    }
}
