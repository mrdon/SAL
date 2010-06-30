package com.atlassian.sal.api.backup;

import java.util.Set;

/**
 * <p>Allows plugins to register for backup. Plugins 'just' have to implement the contract defined by {@link Backup}
 * and the host application will make sure they get backup/restore as part of their own backup.</p>
 *
 * @see Backup
 */
public interface BackupRegistry
{
    /**
     * Registers a backup, this is typically done at plugin install.
     *
     * @param backup the backup to register.
     */
    void register(Backup backup);

    /**
     * <p>Un-register a backup, this is typically done at plugin uninstall.</p>
     * <p>Note that the {@link com.atlassian.sal.api.backup.Backup} object needs to be <em>equal</em>
     * to the one used when {@link #register(Backup) registering} for this method to work properly.</p>
     *
     * @param backup the backup to uninstall.
     */
    void unregister(Backup backup);


    /**
     * <p>Accessor to all the registered backups.</p>
     *
     * @return the registered backups
     */
    Set<Backup> getRegistered();
}
