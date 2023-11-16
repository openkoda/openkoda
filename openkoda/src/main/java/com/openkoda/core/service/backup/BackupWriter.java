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

import com.google.common.collect.ImmutableList;
import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import jakarta.annotation.PostConstruct;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 *  Copy backup file
 */
@Component
public class BackupWriter implements LoggingComponentWithRequestId {

    private static final String ERROR_LOGS_FILE_NAME = "backup_error.log";

    private final String datasourceUrl;
    private final String datasourceUsername;
    private final String datasourcePassword;
    private final String datePattern;
    private final String fileDirectory;
    private final String gpgKeyName;
    private final String gpgKeyFile;
    private final String applicationPropertiesFilePath;
    private final String applicationName;
    private final String scpHost;
    private final String scpTargetDirectory;

    private SimpleDateFormat dateFormat;
    private boolean isWindows;
    private File backupDir;
    private String backupDateInfo;
    private String databaseBackupFile;
    private String tarBackupFile;
    private String scpTargetFile;
    private String pgDumpExecutable;
    private String gpgExecutable;
    private String scpExecutable;

    public BackupWriter(
            @Value("${spring.datasource.url}") String datasourceUrl,
            @Value("${spring.datasource.username}") String datasourceUsername,
            @Value("${spring.datasource.password}") String datasourcePassword,
            @Value("${backup.date.pattern:yyyyMMdd-HHmm}") String datePattern,
            @Value("${backup.file.directory:}") String fileDirectory,
            @Value("${backup.gpg.key.name:}") String gpgKeyName,
            @Value("${backup.gpg.key.file:}") String gpgKeyFile,
            @Value("${backup.application.properties:}") String applicationPropertiesFilePath,
            @Value("${application.name:Default Application}") String applicationName,
            @Value("${backup.scp.host:}") String scpHost,
            @Value("${backup.scp.target:}") String scpTargetDirectory,
            @Value("${backup.scp.executable:scp}") String scpExecutable,
            @Value("${backup.pg_dump.executable:pg_dump}") String pgDumpExecutable,
            @Value("${backup.gpg.executable:gpg}") String gpgExecutable
            ) {

        this.datasourceUrl = datasourceUrl;
        this.datasourceUsername = datasourceUsername;
        this.datasourcePassword = datasourcePassword;
        this.datePattern = datePattern;
        this.fileDirectory = fileDirectory;
        this.gpgKeyName = gpgKeyName;
        this.gpgKeyFile = gpgKeyFile;
        this.applicationPropertiesFilePath = applicationPropertiesFilePath;
        this.applicationName = applicationName;
        this.scpHost = scpHost;
        this.scpTargetDirectory = scpTargetDirectory;
        this.gpgExecutable = gpgExecutable;
        this.scpExecutable = scpExecutable;
        this.pgDumpExecutable = pgDumpExecutable;

    }

    @PostConstruct
    void init() {
        dateFormat = new SimpleDateFormat(datePattern);
        isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
    }

    public File getBackupDir() {
        return backupDir;
    }

    public String getTarBackupFile() {
        return tarBackupFile;
    }

    public String getDatabaseBackupFile() {
        return databaseBackupFile;
    }

    public String getApplicationPropertiesFilePath() {
        return applicationPropertiesFilePath;
    }

    public String getScpTargetFile() {
        return scpTargetFile;
    }

    String getScpTargetDirectory() {
        return scpTargetDirectory;
    }

    public String getFileDirectory() {
        return fileDirectory;
    }

    /**
     * <p>Creating backup directory and do backup</>
     */
    public boolean doBackup(Collection<BackupOption> backupOptions) {
        try {
            debug("[doBackup] execution");
            createBackupDirectoryCommand();
            doDatabaseBackup(backupOptions);
            return doTarEncrypt(backupOptions);
        } catch (IOException | InterruptedException e) {
            error(e, "Backup creation error due to {}. See {} for more info.", e.getMessage(), ERROR_LOGS_FILE_NAME);
        }

        return false;
    }

    private void createBackupDirectoryCommand() {
        debug("[createBackupDirectoryCommand]");
        backupDir = new File(fileDirectory);
        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }
    }

    private void doDatabaseBackup(Collection<BackupOption> backupOptions) throws InterruptedException, IOException {
        if (backupOptions.contains(BackupOption.BACKUP_DATABASE)) {
            pgDumpCommand().start().waitFor();
        }
    }

    private ProcessBuilder pgDumpCommand() {
        debug("[pgDumpCommand]");
        String databaseName = datasourceUrl.substring(datasourceUrl.lastIndexOf("/") + 1);
        String databaseHost = StringUtils.substringBetween(datasourceUrl, "://", ":");
        backupDateInfo = "_" + dateFormat.format(new Date());
        databaseBackupFile = backupDir.getPath() + File.separator + databaseName + backupDateInfo + ".sql";
        ProcessBuilder builder = new ProcessBuilder(
                pgDumpExecutable,
                "-U", datasourceUsername,
                "-h", databaseHost,
                "-d", databaseName,
                "-f", databaseBackupFile);

        builder.environment().put("PGPASSWORD", datasourcePassword);
        builder.redirectError(ProcessBuilder.Redirect.appendTo(new File(ERROR_LOGS_FILE_NAME)));

        return builder;
    }

    private boolean doTarEncrypt(Collection<BackupOption> backupOptions) throws InterruptedException, IOException {
        debug("[doTarEncrypt]");
        List<String> backupPaths = getPathsToBackup(backupOptions);
        if (isWindows || backupPaths.isEmpty()) {
            return false;
        }

        tarCommand(backupPaths).start().waitFor();
        encryptWithGpg();

        return true;
    }

    private List<String> getPathsToBackup(Collection<BackupOption> backupOptions) {
        List<String> paths = new ArrayList<>();
        if (backupOptions.contains(BackupOption.BACKUP_DATABASE)) {
            paths.add(databaseBackupFile);
        }

        if (backupOptions.contains(BackupOption.BACKUP_PROPERTIES)) {
            paths.add(applicationPropertiesFilePath);
        }

        return paths;
    }

    private ProcessBuilder tarCommand(List<String> backupPaths) {
        debug("[tarCommand]");
        tarBackupFile = backupDir.getPath()
                + File.separator + applicationName.replaceAll("\\s", "")
                + backupDateInfo + ".tar.gz";

        List<String> command = ImmutableList.<String>builder()
                .add("tar", "-czf", tarBackupFile)
                .addAll(backupPaths)
                .build();

        ProcessBuilder builder = new ProcessBuilder(command);
        builder.redirectError(ProcessBuilder.Redirect.appendTo(new File(ERROR_LOGS_FILE_NAME)));

        return builder;
    }

    private void encryptWithGpg() throws InterruptedException, IOException {
        debug("[encryptWithGpg]");
        if (!gpgKeyName.isEmpty()) {
            boolean gpgKeyAvailable = gpgExists();
//            if the gpg key does not exist the system should load one
            if (!gpgKeyAvailable) {
                loadGpgKey().start().waitFor();
//                making sure the gpg key got loaded correctly and it exists
                gpgKeyAvailable = gpgExists();
            }
            if (gpgKeyAvailable) {
                encryptGpgCommand().start().waitFor();
            }
        }
    }

    private boolean gpgExists() {
        debug("[gpgExists]");
        try {
            String output = IOUtils.toString(checkGpgKeyCommand().start().getInputStream());
            return output.contains(gpgKeyName);
        } catch (IOException e) {
            error("Cannot check if GPG key exists", e);
        }

        return false;
    }

    private ProcessBuilder checkGpgKeyCommand() {
        debug("[checkGpgKeyCommand]");
        ProcessBuilder builder = new ProcessBuilder(
                gpgExecutable,
                "-k", gpgKeyName);
        builder.redirectError(ProcessBuilder.Redirect.appendTo(new File(ERROR_LOGS_FILE_NAME)));

        return builder;
    }

    private ProcessBuilder loadGpgKey() {
        debug("[loadGpgKey]");
        ProcessBuilder builder = new ProcessBuilder(
                gpgExecutable,
                "--import", gpgKeyFile);
        builder.redirectError(ProcessBuilder.Redirect.appendTo(new File(ERROR_LOGS_FILE_NAME)));

        return builder;
    }

    private ProcessBuilder encryptGpgCommand() {
        debug("[encryptGpgCommand]");
        ProcessBuilder builder = new ProcessBuilder(
                gpgExecutable, "-e", "--always-trust",
                "-r", gpgKeyName,
                tarBackupFile);
        builder.redirectError(ProcessBuilder.Redirect.appendTo(new File(ERROR_LOGS_FILE_NAME)));

        return builder;
    }

    /**
     * <p> Trigger scp command method </p>
     *
     * @return true when successful
     */

    public boolean copyBackupFile(Collection<BackupOption> backupOptions, File fileToCopy) {
        try {
            if (isScpEnabled(backupOptions) && isAccessible(fileToCopy)) {
                debug("[copyBackupFile] execution");
                int result = scpCommand(fileToCopy).start().waitFor();
                return 0 == result;
            }
        } catch (InterruptedException | IOException e) {
            error(e, "SCP execution error due to {}. See {} for more info.", e.getMessage(), ERROR_LOGS_FILE_NAME);
        }

        return false;
    }

    private boolean isScpEnabled(Collection<BackupOption> backupOptions) {
        return !isWindows && backupOptions.contains(BackupOption.SCP_ENABLED);
    }

    private boolean isAccessible(File file) {
        return null != file && file.exists() && file.canRead();
    }


    /**
     * Copy file
     *
     * @param path to file
     */
    private ProcessBuilder scpCommand(File path) {
        debug("[scpCommand]");
        String scpSourceFile = path.getAbsolutePath();

        // copy te remote if host is specified
        scpTargetFile = scpHost.isEmpty() ? "" : scpHost + ":";
        scpTargetFile += scpTargetDirectory + File.separator + path.getName();

        ProcessBuilder builder = new ProcessBuilder(
                scpExecutable,
                scpSourceFile,
                scpTargetFile);

        builder.redirectError(ProcessBuilder.Redirect.appendTo(new File(ERROR_LOGS_FILE_NAME)));

        return builder;
    }

}
