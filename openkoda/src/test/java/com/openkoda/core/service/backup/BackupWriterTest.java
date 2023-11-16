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

package com.openkoda.core.service.backup;

import com.openkoda.AbstractTest;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

import static com.openkoda.core.service.backup.BackupOption.*;
import static java.util.Collections.emptySet;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class BackupWriterTest extends AbstractTest {

    @Autowired
    private BackupWriter testObject;

    @BeforeEach
    public void setup() {
        List<File> fileDirs = List.of(
                new File(testObject.getFileDirectory()),
                new File(testObject.getScpTargetDirectory()));

        for (File fileDir : fileDirs) {
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
        }
    }

    @AfterEach
    public void cleanupBackupFiles() throws IOException {
        List<File> fileDirs = List.of(
                new File(testObject.getFileDirectory()),
                new File(testObject.getScpTargetDirectory()));

        for (File fileDir : fileDirs) {
            if (fileDir.exists()) {
                FileUtils.cleanDirectory(fileDir);
            }
        }
    }

    @Test
    public void shouldNotCreateBackupWithEmptyOptions() {

        // GIVEN
        Set<BackupOption> backupOptions = emptySet();

        // WHEN
        boolean result = testObject.doBackup(backupOptions);

        // THEN
        assertFileDoesNotExist(testObject.getDatabaseBackupFile());
        assertFileDoesNotExist(testObject.getTarBackupFile());
        assertFalse(result);
    }

    @Test
    @DisabledOnOs(OS.LINUX)
    public void shouldCreateDatabaseBackup_windowsEnv() {

        // GIVEN
        Set<BackupOption> backupOptions = Set.of(BACKUP_DATABASE);

        // WHEN
        boolean result = testObject.doBackup(backupOptions);

        // THEN
        assertFileExists(testObject.getDatabaseBackupFile());
        assertFileDoesNotExist(testObject.getTarBackupFile());
        assertFalse(result);
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    public void shouldCreateDatabaseBackup_nonWindowsEnv() throws IOException {

        // GIVEN
        Set<BackupOption> backupOptions = Set.of(BACKUP_DATABASE);

        // WHEN
        boolean result = testObject.doBackup(backupOptions);

        // THEN
        assertFileExists(testObject.getDatabaseBackupFile());
        assertFileExists(testObject.getTarBackupFile());
        assertTarContains(testObject.getDatabaseBackupFile());
        Assertions.assertTrue(result);
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    public void shouldCreatePropertiesBackup_nonWindowsEnv() throws IOException {

        // GIVEN
        Set<BackupOption> backupOptions = Set.of(BACKUP_PROPERTIES);

        // WHEN
        boolean result = testObject.doBackup(backupOptions);

        // THEN
        assertFileDoesNotExist(testObject.getDatabaseBackupFile());
        assertFileExists(testObject.getTarBackupFile());
        assertTarContains(testObject.getApplicationPropertiesFilePath());
        Assertions.assertTrue(result);
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    public void shouldCreateFullBackup_nonWindowsEnv() throws IOException {

        // GIVEN
        Set<BackupOption> backupOptions = Set.of(BACKUP_PROPERTIES, BACKUP_DATABASE);

        // WHEN
        boolean result = testObject.doBackup(backupOptions);

        // THEN
        assertFileExists(testObject.getDatabaseBackupFile());
        assertFileExists(testObject.getTarBackupFile());
        assertTarContains(testObject.getDatabaseBackupFile(), testObject.getApplicationPropertiesFilePath());
        Assertions.assertTrue(result);
    }

    @Test
    public void shouldNotCopyBackupWithEmptyOptions() throws IOException {

        // GIVEN
        Set<BackupOption> backupOptions = emptySet();
        File fileToCopy = getTmpFile(true);

        // WHEN
        boolean result = testObject.copyBackupFile(backupOptions, fileToCopy);

        // THEN
        assertFalse(result);
    }

    @Test
    @DisabledOnOs(OS.LINUX)
    public void shouldNotCopyBackup_windowsEnv() throws IOException {

        // GIVEN
        Set<BackupOption> backupOptions = Set.of(SCP_ENABLED);
        File fileToCopy = getTmpFile(true);

        // WHEN
        boolean result = testObject.copyBackupFile(backupOptions, fileToCopy);

        // THEN
        assertFalse(result);
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    public void shouldCopyBackup_nonWindowsEnv() throws IOException {

        // GIVEN
        Set<BackupOption> backupOptions = Set.of(SCP_ENABLED);
        File fileToCopy = getTmpFile(true);

        // WHEN
        boolean result = testObject.copyBackupFile(backupOptions, fileToCopy);

        // THEN
        Assertions.assertTrue(result);
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    public void shouldNotCopyBackupWhenItDoesNotExist_nonWindowsEnv() throws IOException {

        // GIVEN
        Set<BackupOption> backupOptions = Set.of(SCP_ENABLED);
        File fileToCopy = getTmpFile(false);

        // WHEN
        boolean result = testObject.copyBackupFile(backupOptions, fileToCopy);

        // THEN
        assertFalse(result);
    }

    private void assertFileExists(String stringPath) {
        Assertions.assertTrue(fileExists(stringPath));
    }

    private void assertFileDoesNotExist(String stringPath) {
        Assertions.assertTrue(nullOrFileDoesNotExist(stringPath));
    }

    private boolean nullOrFileDoesNotExist(String stringPath) {
        return null == stringPath || !fileExists(stringPath);
    }

    private boolean fileExists(String stringPath) {
        return Files.exists(Paths.get(stringPath));
    }

    private void assertTarContains(String... paths) throws IOException {

        // remove leading slash to match tar content
        Stream<String> pathsStream = Stream.of(paths).filter(StringUtils::isNotBlank).map(path -> path.substring(1));

        TarArchiveInputStream tarStream = getTarStream();
        List<String> tarFiles = new ArrayList<>();

        ArchiveEntry entry;
        while ((entry = tarStream.getNextEntry()) != null) {
            tarFiles.add(entry.getName());
        }

        MatcherAssert.assertThat(tarFiles, containsInAnyOrder(pathsStream.toArray()));
    }

    private TarArchiveInputStream getTarStream() throws IOException {
        Path path = Paths.get(testObject.getTarBackupFile());
        InputStream inputStream = Files.newInputStream(path);
        GZIPInputStream gzipStream = new GZIPInputStream(inputStream);

        return new TarArchiveInputStream(gzipStream);
    }

    private File getTmpFile(boolean create) throws IOException {
        String tmpFileStringPath = testObject.getFileDirectory() + File.separator + "file.tmp";
        File fileToCopy = new File(tmpFileStringPath);
        if (create) {
            Files.write(fileToCopy.toPath(), "temp_data".getBytes());
        }

        return fileToCopy;
    }

}