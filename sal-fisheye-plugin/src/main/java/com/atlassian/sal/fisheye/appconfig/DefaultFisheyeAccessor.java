package com.atlassian.sal.fisheye.appconfig;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import com.atlassian.fisheye.event.CommitEvent;
import com.cenqua.fisheye.AppConfig;
import com.cenqua.fisheye.cache.RevisionCache;
import com.cenqua.fisheye.config.ConfigException;
import com.cenqua.fisheye.config.RepositoryManager;
import com.cenqua.fisheye.config.RootConfig;
import com.cenqua.fisheye.config1.LinkerSimpleType;
import com.cenqua.fisheye.config1.LinkerType;
import com.cenqua.fisheye.config1.RepositoryType;
import com.cenqua.fisheye.config1.SvnRepType;
import com.cenqua.fisheye.rep.ChangeSet;
import com.cenqua.fisheye.rep.DbException;
import com.cenqua.fisheye.rep.RepositoryEngine;
import com.cenqua.fisheye.rep.RepositoryHandle;
import com.cenqua.fisheye.rep.RepositoryHandle.StateException;
import com.cenqua.fisheye.util.Disposer;
import com.cenqua.fisheye.util.XmlbeansUtil;
import com.cenqua.fisheye.web.admin.actions.svn.SvnSymbolicHelper;

public class DefaultFisheyeAccessor implements FisheyeAccessor
{
    private static final Logger log = Logger.getLogger(DefaultFisheyeAccessor.class);
    private static final int REPO_CHILD_INDENT = 6;
    private static final int REPO_INDENT = 2;

    public ChangeSet getCommitChangeSet(final CommitEvent commitEvent)
    {
        try
        {
            Disposer.pushThreadInstance();
            final String repositoryName = commitEvent.getRepositoryName();
            final String changeSetId = commitEvent.getChangeSetId();
            final RepositoryManager repositoryManager = AppConfig.getsConfig().getRepositoryManager();
            final RepositoryHandle repositoryHandle = repositoryManager.getRepository(repositoryName);
            final RepositoryEngine repositoryEngine = repositoryHandle.acquireEngine();
            final RevisionCache revisionCache = repositoryEngine.getRevisionCache();
            return revisionCache.getChangeSet(changeSetId);
        } catch (final DbException e)
        {
            log.error("Error getting changeset", e);
        } catch (final StateException e)
        {
            log.error("Error getting changeset", e);
        } finally
        {
            Disposer.popThreadInstance();
        }
        return null;
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


    private int getRepositoryId(final String repositoryKey)
    {
        final RepositoryType[] repositoryArray = AppConfig.getsConfig().getConfig().getRepositoryArray();
        for (int i = 0; i < repositoryArray.length; i++)
        {
            final RepositoryType repositoryType = repositoryArray[i];
            if (repositoryKey.equals(repositoryType.getName()))
            {
                return i;
            }
        }
        return -1;
    }

}
