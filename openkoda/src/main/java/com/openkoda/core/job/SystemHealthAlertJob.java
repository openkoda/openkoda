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

package com.openkoda.core.job;

import com.openkoda.core.audit.SystemHealthStatus;
import com.openkoda.core.helper.SystemStatHelper;
import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import jakarta.inject.Inject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Job checking system status.
 * Gets information such as ram usage, cpu usage and free disk space.
 * See also {@link SystemStatHelper}
 */
@Component
public class SystemHealthAlertJob implements LoggingComponentWithRequestId {

    @Inject
    SystemStatHelper statHelper;

    @Value("${max.disk.percentage:75}")
    double maxUsedDiskSpacePercentageAllowed;

    @Value("${max.ram.percentage:75}")
    double maxUsedRamSpacePercentageAllowed;

    @Value("${max.cpu.percentage:75}")
    double maxCpuUsagePercentageAllowed;

    /**
     * Checks system status.
     * Reviews the RAM usage, CPU usage and disk usage.
     * It produces error logs for any values outreaching max limits.
     */
    public void checkSystem() {
        debug("[checkSystem]");
        SystemHealthStatus systemHealthStatus = statHelper.statusNow();
        double usedRam = ((double) systemHealthStatus.getTotalHeapMemory() / systemHealthStatus.getMaxHeapMemory()) * 100.0;
        double totalSpace = (double) systemHealthStatus.getPartitions().stream()
                .map(systemHealthStatus::getTotalSpace)
                .reduce(Long::sum)
                .orElse(-1L);
        double freeSpace = (double) systemHealthStatus.getPartitions().stream()
                .map(systemHealthStatus::getFreeSpace)
                .reduce(Long::sum)
                .orElse(-1L);

        if (usedRam > maxUsedRamSpacePercentageAllowed) {
            error("[checkSystem] RAM usage is above allowed levels. Currently at {} ", usedRam);
        }

        if (totalSpace > 0 && freeSpace > 0) {
            double usedDiskSpace = (1L - freeSpace / totalSpace) * 100.0;
            if (usedDiskSpace > maxUsedDiskSpacePercentageAllowed) {
                error("[checkSystem] Disk usage is above allowed levels. Currently at {} ", usedDiskSpace);
            }
        } else {
            error("[checkSystem] Error occurred when calculating disk usage");
        }
        if (systemHealthStatus.isSysstatEnabled()) {
            double cpuUsage = getCpuUsage(systemHealthStatus);
            if (cpuUsage > maxCpuUsagePercentageAllowed) {
                error("[checkSystem] CPU usage is above allowed levels. Currently at {} ", cpuUsage);
            }
        }
    }

    private double getCpuUsage(SystemHealthStatus systemHealthStatus) {
        int cpuIndex = Arrays.asList(systemHealthStatus.getPidstatHeader()).indexOf("%CPU");
        return systemHealthStatus.getPidstatData().stream()
                .map(row -> Double.parseDouble(row[cpuIndex]))
                .reduce(Double::sum).orElse(-1.0);
    }
}
