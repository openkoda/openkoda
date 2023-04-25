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

package com.openkoda.core.helper;

import com.openkoda.core.audit.SystemHealthStatus;
import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import jakarta.persistence.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class provides methods to fill System Statuses with data from database and system.
 */
@Service
public class SystemStatHelper implements LoggingComponentWithRequestId {

    @Value("${system.cat.executable:cat}")
    private String catExecutable;
    @Value("${system.apt.executable:apt}")
    private String aptExecutable;
    @Value("${system.pidstat.executable:pidstat}")
    private String pidstatExecutable;
    @PersistenceUnit
    EntityManagerFactory entityManagerFactory;

    public static boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");

    public SystemHealthStatus statusNow() {
        debug("[statusNow]");
        SystemHealthStatus status = new SystemHealthStatus();
        status.setMaxHeapMemory(Runtime.getRuntime().maxMemory());
        status.setFreeHeapMemory(Runtime.getRuntime().freeMemory());
        status.setTotalHeapMemory(Runtime.getRuntime().totalMemory());
        for (Path root : FileSystems.getDefault().getRootDirectories()) {
            File rootFile = root.toFile();
            status.setDiskSpace(rootFile.getPath(), rootFile.getFreeSpace(), rootFile.getTotalSpace());

        }
        status.setDbLogStatement(getLogStatement());
        status.setLogMinDurationStatement(getLogMinDurationStatement());
        if (isWindows) {
            status.setIsWindows(true);
            return status;
        }
        if (!isSysstatInstalled()) {
            status.setSysstatInstalled(false);
            return status;
        }
        status.setSysstatInstalled(true);
        if (!isSysstatEnabled()) {
            status.setSysstatEnabled(false);
            return status;
        }
        status.setSysstatEnabled(true);
        status.setPidstatData(getPidstatRows());
        return status;
    }

    private List<String[]> getPidstatRows() {
        debug("[getPidstatRows]");
        List<String[]> cells = new ArrayList<>();
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(pidstatExecutable, "-urd", "-h");
            Process p = processBuilder.start();
            p.waitFor();
            String pidstat = IOUtils.toString(p.getInputStream());
            String errors = IOUtils.toString(p.getErrorStream());
            if (StringUtils.isNotBlank(errors)) {
                error("Error during running pidstat: \n{}", errors);
            }
            List<String> lines = Arrays.asList(pidstat.split("\\n"));
            lines = lines.subList(2, lines.size());
            for (String s : lines) {
                String[] tmp = s.split("\\s+");
                String firstElem = tmp[0] + " " + tmp[1];
                tmp = Arrays.copyOfRange(tmp, 1, tmp.length);
                tmp[0] = firstElem;
                cells.add(tmp);
            }
        } catch (IOException | InterruptedException e) {
            error("Error during getting pidstat data", e);
        }
        return cells;
    }

    public String getLogMinDurationStatement() {
        debug("[getLogMinDurationStatement]");
        String result;
        EntityManager em = null;
        try {
            em = entityManagerFactory.createEntityManager();
            em.setFlushMode(FlushModeType.AUTO);
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            result = (String) em.createNativeQuery("SELECT setting FROM pg_settings WHERE name = 'log_min_duration_statement'").getSingleResult();
            transaction.commit();
            em.close();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
                em = null;
            }
        }
        return result;
    }

    public String getLogStatement() {
        debug("[getLogStatement]");
        String result;
        EntityManager em = null;
        try {
            em = entityManagerFactory.createEntityManager();
            em.setFlushMode(FlushModeType.AUTO);
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            result = (String) em.createNativeQuery("SELECT setting FROM pg_settings WHERE name = 'log_statement'").getSingleResult();
            transaction.commit();
            em.close();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
                em = null;
            }
        }
        return result;
    }

    public boolean isSysstatInstalled() {
        debug("[isSysstatInstalled]");
        if (isWindows) {
            return false;
        }
        try {
            ProcessBuilder pBuilder = new ProcessBuilder(aptExecutable, "list", "--installed", "sysstat");
            Process p = pBuilder.start();
            p.waitFor();
            String aptSysstat = IOUtils.toString(p.getInputStream());
            debug("[isSysstatInstalled] output: {}", aptSysstat);
            if (aptSysstat.contains("sysstat")) {
                return true;
            }
        } catch (Exception e) {
            error(e,"Error occurred when trying to determine availability of sysstat");
        }
        return false;
    }

    private boolean isSysstatEnabled() {
        debug("[isSysstatEnabled]");
        try {
            ProcessBuilder pBuilder = new ProcessBuilder(catExecutable, "/etc/default/sysstat");
            Process p = pBuilder.start();
            p.waitFor();
            String sysstat = IOUtils.toString(p.getInputStream());
            if (sysstat.contains("ENABLED=\"true\"")) {
                return true;
            }
        } catch (IOException | InterruptedException e) {
            error("Error occurred when trying to check if sysstat enabled", e);
        }
        return false;
    }
}
