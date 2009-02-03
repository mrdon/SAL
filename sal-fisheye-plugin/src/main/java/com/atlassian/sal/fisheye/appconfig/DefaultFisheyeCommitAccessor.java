package com.atlassian.sal.fisheye.appconfig;

import org.apache.log4j.Logger;

import com.atlassian.fisheye.event.CommitEvent;
import com.cenqua.fisheye.AppConfig;
import com.cenqua.fisheye.cache.RevisionCache;
import com.cenqua.fisheye.config.RepositoryManager;
import com.cenqua.fisheye.rep.ChangeSet;
import com.cenqua.fisheye.rep.DbException;
import com.cenqua.fisheye.rep.RepositoryEngine;
import com.cenqua.fisheye.rep.RepositoryHandle;
import com.cenqua.fisheye.rep.RepositoryHandle.StateException;
import com.cenqua.fisheye.util.Disposer;

public class DefaultFisheyeCommitAccessor implements FisheyeCommitAccessor
{
    private static final Logger log = Logger.getLogger(DefaultFisheyeCommitAccessor.class);

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
}
