/*
MIT License

Copyright (c) 2016-2023, Openkoda CDX Sp. z o.o. Sp. K. <openkoda.com>

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

package com.openkoda.service;

import com.openkoda.AbstractTest;
import com.openkoda.core.service.BackupService;
import com.openkoda.dto.system.ScheduledSchedulerDto;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;

/**
 * JUnits for {@link BackupService}
 *
 * @author Martyna Litkowska (mlitkowska@stratoflow.com)
 * @since 2019-03-28
 */
public class BackupServiceTest extends AbstractTest {

    @Autowired
    private BackupService backupService;

    @AfterEach
    public void cleanupBackupFiles() throws IOException {
        File backupDir = backupService.getBackupDir();
        if(backupDir != null && backupDir.exists()) {
            FileUtils.cleanDirectory(backupDir);
        }
    }

    @Test
    public void doFullBackup_nullParam() {
//        given


//        when
        boolean result = backupService.doFullBackup(null);

//        then
        assertFalse(result);
    }

    @Test
    public void doFullBackup_badParam() {
//        given
        ScheduledSchedulerDto consumerParam = new ScheduledSchedulerDto("", "badParamWhichIsNotEqualBackup", -1L,false, LocalDateTime.now());

//        when
        boolean result = backupService.doFullBackup(consumerParam);

//        then
        assertFalse(result);
    }

    @Test
    public void doFullBackup_windowsEnv() {
//        given
        ScheduledSchedulerDto consumerParam = new ScheduledSchedulerDto("", "backup", -1L, false, LocalDateTime.now());
        assumeTrue(System.getProperty("os.name").toLowerCase().startsWith("windows"));

//        when
        boolean result = backupService.doFullBackup(consumerParam);

//        then
        assertFalse(result);
    }

    @Test
    public void doFullBackup_notWindowsEnv() {
//        given
        ScheduledSchedulerDto consumerParam = new ScheduledSchedulerDto("", "backup", -1L, false, LocalDateTime.now());
        assumeFalse(System.getProperty("os.name").toLowerCase().startsWith("windows"));

//        when
        boolean result = backupService.doFullBackup(consumerParam);

//        then
        assertTrue(result);
    }

}
