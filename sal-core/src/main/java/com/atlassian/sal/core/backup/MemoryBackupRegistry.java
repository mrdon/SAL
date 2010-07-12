package com.atlassian.sal.core.backup;

import com.atlassian.sal.api.backup.Backup;
import com.atlassian.sal.api.backup.BackupRegistry;
import com.google.common.collect.ImmutableSet;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Memory implementation of the {@link com.atlassian.sal.api.backup.BackupRegistry} that should suit most needs.
 * // TODO should we split the register/unregister from the getRegistered?
 */
public class MemoryBackupRegistry implements BackupRegistry
{
    private final Set<Backup> backups;

    public MemoryBackupRegistry()
    {
        backups = new CopyOnWriteArraySet<Backup>();
    }

    public void register(Backup backup)
    {
        backups.add(backup);
    }

    public void unregister(Backup backup)
    {
        backups.remove(backup);
    }

    public Set<Backup> getRegistered()
    {
        return ImmutableSet.copyOf(backups);
    }
}
