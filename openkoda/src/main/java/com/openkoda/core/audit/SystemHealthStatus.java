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

package com.openkoda.core.audit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Data object for keeping system health information.
 * See {@link com.openkoda.core.helper.SystemStatHelper}
 */
public class SystemHealthStatus {

    /**
     * Current size of heap in bytes
     */
    private long totalHeapMemory;
    /**
     * Maximum size of heap in bytes
     */
    private long maxHeapMemory;

    /**
     * Free memory within the heap in bytes
     */
    private long freeHeapMemory;

    /**
     * log_statement information from pg_settings
     * See {@link com.openkoda.core.helper.SystemStatHelper#getLogStatement}
     */
    private String dbLogStatement;


    /**
     * log_min_duration_statement information from pg_settings
     * See {@link com.openkoda.core.helper.SystemStatHelper#getLogMinDurationStatement}
     */
    private String logMinDurationStatement;

    /**
     * Should be true when application runs on Windows
     */
    private boolean isWindows;
    /**
     * Should be true when runs on linux and sysstat tool enabled
     */
    private boolean sysstatEnabled;
    /**
     * Should be true when runs on linux and sysstat tool installed
     */
    private boolean sysstatInstalled;

    /**
     * Keeps free disk space for drives
     */
    private Map<String, Long> freePartitionSpace = new HashMap<>();
    /**
     * Keeps total disk space for drives
     */
    private Map<String, Long> totalPartitionSpace = new HashMap<>();

    /**
     * Keeps processes data read from pidstat (if sysstat installed)
     */
    private List<String[]> pidstatData;
    /**
     * Keeps processes headers from pidstat (if sysstat installed)
     */
    private String[] pidstatHeader;

    public void setDiskSpace(String path, long freeSpace, long totalSpace) {
        freePartitionSpace.put(path, freeSpace);
        totalPartitionSpace.put(path, totalSpace);
    }

    public Set<String> getPartitions() {
        return freePartitionSpace.keySet();
    }

    public Long getFreeSpace(String partition) {
        return freePartitionSpace.get(partition);
    }

    public Long getTotalSpace(String partition) {
        return totalPartitionSpace.get(partition);
    }

    public long getTotalHeapMemory() {
        return totalHeapMemory;
    }

    public void setTotalHeapMemory(long totalHeapMemory) {
        this.totalHeapMemory = totalHeapMemory;
    }

    public long getMaxHeapMemory() {
        return maxHeapMemory;
    }

    public void setMaxHeapMemory(long maxHeapMemory) {
        this.maxHeapMemory = maxHeapMemory;
    }

    public long getFreeHeapMemory() {
        return freeHeapMemory;
    }

    public void setFreeHeapMemory(long freeHeapMemory) {
        this.freeHeapMemory = freeHeapMemory;
    }

    public String getDbLogStatement() {
        return dbLogStatement;
    }

    public void setDbLogStatement(String dbLogStatement) {
        this.dbLogStatement = dbLogStatement;
    }

    public String getLogMinDurationStatement() {
        return logMinDurationStatement;
    }

    public void setLogMinDurationStatement(String logMinDurationStatement) {
        this.logMinDurationStatement = logMinDurationStatement;
    }

    public void setSysstatEnabled(boolean systatEnabled) {
        this.sysstatEnabled = systatEnabled;
    }

    public boolean isSysstatEnabled() {
        return sysstatEnabled;
    }

    public void setSysstatInstalled(boolean systatEnabled) {
        this.sysstatInstalled = systatEnabled;
    }

    public boolean isSysstatInstalled() {
        return sysstatInstalled;
    }

    public boolean isWindows() {
        return isWindows;
    }

    public void setIsWindows(boolean isWindows) {
        this.isWindows = isWindows;
    }

    public void setPidstatData(List<String[]> pidstatRows) {
        this.pidstatHeader = pidstatRows.get(0);
        this.pidstatData = pidstatRows.subList(1, pidstatRows.size());
    }

    public List<String[]> getPidstatData() {
        return this.pidstatData;
    }

    public String[] getPidstatHeader() {
        return pidstatHeader;
    }
}
