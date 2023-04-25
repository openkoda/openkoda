/*
MIT License

Copyright (c) 2016-2022, Codedose CDX Sp. z o.o. Sp. K. <stratoflow.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
documentation files (the "Software"), to deal in the Software without restriction, including without limitation
the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice
shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR
A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.openkoda.core.service;

import com.openkoda.controller.ComponentProvider;
import com.openkoda.core.service.backup.BackupOption;
import com.openkoda.core.service.backup.BackupWriter;
import com.openkoda.dto.system.ScheduledSchedulerDto;
import jakarta.inject.Inject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

import static com.openkoda.core.service.event.ApplicationEvent.BACKUP_CREATED;
import static com.openkoda.core.service.event.ApplicationEvent.BACKUP_FILE_COPIED;

/**
 * Service performing backup logic and providing consumer for that
 * Backup is done by jenkins user performing unix commands in the directory where currently running application jar is
 * placed.
 * Full backup performs:
 *  * pg_dump on the database,
 *  * tar -czf on database backup file and application.properties
 *  * checking if gpg key is available
 *  * if gpg key is not available then importing it from the filesystem
 *  * encryption of tar archive with gpg
 *  * emission on BACKUP_CREATED event
 *
 * @author Martyna Litkowska (mlitkowska@stratoflow.com)
 * @since 2019-03-26
 */
@Service
public class BackupService extends ComponentProvider {

    @Inject
    private BackupWriter backupWriter;

    @Value("${backup.options:BACKUP_DATABASE,BACKUP_PROPERTIES}")
    private List<BackupOption> backupOptions;

    public static final String BACKUP = "backup";

    public File getBackupDir() {
        return backupWriter.getBackupDir();
    }

    /**
     * Consumer for full backups
     */
    public boolean doFullBackup(ScheduledSchedulerDto eventParameter) {
        return isBackupEvent(eventParameter) && doBackup();
    }

    private boolean isBackupEvent(ScheduledSchedulerDto eventParameter) {
        return eventParameter != null && eventParameter.eventData.toLowerCase().equals(BACKUP);
    }

    private boolean doBackup() {
        info("[doBackup]");
        if (backupWriter.doBackup(backupOptions)) {
            emitBackupCreated();
            return true;
        }

        return false;
    }

    private void emitBackupCreated() {
        debug("[emitBackupCreated]");
        String tarBackupFile = backupWriter.getTarBackupFile();
        services.applicationEvent.emitEvent(BACKUP_CREATED, new File(tarBackupFile));
    }

    /**
     * Consumer for copying backup archive to remote host
     */
    public boolean copyBackupFile(File file) {
        debug("[copyBackupFile]");
        if (backupWriter.copyBackupFile(backupOptions, file)) {
            emitBackupFileCopied();
            return true;
        }

        return false;
    }

    private void emitBackupFileCopied() {
        debug("[emitBackupFileCopied]");
        String scpTargetFile = backupWriter.getScpTargetFile();
        services.applicationEvent.emitEvent(BACKUP_FILE_COPIED, scpTargetFile);
    }

}
