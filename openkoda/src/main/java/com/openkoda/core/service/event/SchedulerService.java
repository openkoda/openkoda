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

package com.openkoda.core.service.event;

import com.openkoda.controller.ComponentProvider;
import com.openkoda.core.helper.ClusterHelper;
import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.core.tracker.RequestIdHolder;
import com.openkoda.dto.system.ScheduledSchedulerDto;
import com.openkoda.model.component.Scheduler;
import jakarta.inject.Inject;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

/**
 * Service for performing any actions related to scheduling.
 * It allows to  register any new {@link Scheduler}, do the re-scheduling operation and removing already scheduled tasks.
 *
 * @author Martyna Litkowska (mlitkowska@stratoflow.com)
 * @since 2019-03-20
 */
@Service
public class SchedulerService extends ComponentProvider implements HasSecurityRules {


    /**
     * Map stores currently scheduled tasks which makes it possible to remove them later (switch jobs off) when needed.
     */
    private Map<Long, ScheduledFuture> currentlyScheduled = new HashMap<>();

    private final TaskScheduler taskScheduler;

    @Inject
    private ClusterEventSenderService clusterEventSenderService;

    @Autowired
    public SchedulerService(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    /**
     * This method retrieves all Scheduler objects from the database using repositories.unsecure.scheduler.findAll(),
     * and schedules each of them using the schedule method.
     *
     * @return true if all schedulers are successfully scheduled, or false if any scheduler fails to schedule.
     */
    public boolean scheduleAllFromDb() {
        debug("[scheduleAllFromDb]");
        repositories.unsecure.scheduler.findAll().forEach(this::schedule);
        return true;
    }

    /**
     * This method load and schedules a task with the given schedulerId.
     */
    public boolean loadClusterAware(long schedulerId) {
        debug("[loadClusterAware] {}", schedulerId);
        if (ClusterHelper.isCluster()) {
            return clusterEventSenderService.loadScheduler(schedulerId);
        }
        Scheduler s = repositories.unsecure.scheduler.findOne(schedulerId);
        return schedule(s);
    }

    /**
     * This method remove scheduler with the given schedulerId.
     */
    public boolean removeClusterAware(long schedulerId) {
        debug("[removeClusterAware] {}", schedulerId);
        if (ClusterHelper.isCluster()) {
            return clusterEventSenderService.removeScheduler(schedulerId);
        }
        return remove(schedulerId);
    }

    /**
     * This method is responsible for reloading a scheduled task with the given schedulerId.
     * @param schedulerId
     * @return
     */
    public boolean reloadClusterAware(long schedulerId) {
        debug("[reloadClusterAware] {}", schedulerId);
        if (ClusterHelper.isCluster()) {
            return clusterEventSenderService.reloadScheduler(schedulerId);
        }
        return removeAndLoadFromDb(schedulerId);
    }

    /**
     * This method load scheduler from database and schedule by calling schedule() method
     * @return if the removal is unsuccessful, the method returns false, indicating that the rescheduling failed.
     * @see SchedulerService#schedule(Scheduler)
     */
    public boolean loadFromDb(long schedulerId) {
        debug("[loadFromDb] {}", schedulerId);
        Scheduler s = repositories.unsecure.scheduler.findOne(schedulerId);
        return schedule(s);
    }

    /**
     * This method remove schedule and schedule new one by calling loadFromDb() method
     * @return if the removal is unsuccessful, the method returns false, indicating that the rescheduling failed.
     * @see SchedulerService#loadFromDb(long)
     * @see SchedulerService#remove(Long)
     */
    public boolean removeAndLoadFromDb(long schedulerId) {
        debug("[removeAndLoadFromDb] {}", schedulerId);
        if (remove(schedulerId)) {
            return loadFromDb(schedulerId);
        }
        return false;
    }

    /**
     * This method remove schedule and schedule new one
     * @return if the removal is unsuccessful, the method returns false, indicating that the rescheduling failed.
     * @see SchedulerService#schedule(Scheduler)
     * @see SchedulerService#remove(Long)
     */
    public boolean reschedule(Long schedulerId, Scheduler scheduler) {
        debug("[reschedule] {} {}", schedulerId, scheduler);
        if (remove(schedulerId)) {
            return schedule(scheduler);
        }
        return false;
    }


    /**
     * This method schedules a task to be executed based on a given scheduler object.
     * @param scheduler contains information such as the cron expression for the task, the event data, organization id and whether the task should only be executed on the master node in a cluster.
     * @return false if the scheduler object is null, returns true otherwise.
     */
    @PreAuthorize(CHECK_CAN_MANAGE_EVENT_LISTENERS)
    public boolean schedule(Scheduler scheduler) {
        if (scheduler != null) {
            debug("Scheduling task {} with data {}", scheduler.getCronExpression(), scheduler.getEventData());
            ScheduledSchedulerDto schedulerDto =
                new ScheduledSchedulerDto(
                    scheduler.getCronExpression(),
                    scheduler.getEventData(),
                    scheduler.getOrganizationId(),
                    scheduler.isOnMasterOnly(),
                            scheduler.isAsync(),
                    LocalDateTime.now());

            currentlyScheduled.put(scheduler.getId(),
                    taskScheduler.schedule(
                            new SchedulerTask(schedulerDto), new CronTrigger(scheduler.getCronExpression())
                    )
            );
            return true;
        }
        return false;
    }

    /**
     * This method removing a scheduled task identified by a given scheduler ID.
     * @param schedulerId
     * @return true when removed successfully, false otherwise
     */
    @PreAuthorize(CHECK_CAN_MANAGE_EVENT_LISTENERS)
    public boolean remove(Long schedulerId) {
        ScheduledFuture scheduledFuture = currentlyScheduled.get(schedulerId);
        debug("Removing scheduled task for scheduler ID {}", schedulerId);
        if (scheduledFuture != null) {
            if (scheduledFuture.cancel(false)) {
                debug("Scheduled task for scheduler ID {} removed", schedulerId);
                currentlyScheduled.remove(schedulerId);
                return true;
            }
        }
        return false;
    }

    /**
     * Class used only for wrapping event emission in a Runnable implementation.
     * ScheduledTask is created while registering any new {@link Scheduler}.
     */
    public class SchedulerTask implements Runnable {

        private ScheduledSchedulerDto executedScheduler;

        SchedulerTask(ScheduledSchedulerDto executedScheduler) {
            this.executedScheduler = executedScheduler;
        }

        @Override
        public void run() {
            MDC.put(RequestIdHolder.PARAM_CRON_JOB_ID, RequestIdHolder.generate());
            if (executedScheduler.onMasterOnly && not(ClusterHelper.isMaster())) {
                debug("[SchedulerTask] {}, not master, skipping.", executedScheduler.notificationMessage());
                return;
            }
            debug("[SchedulerTask] {}", executedScheduler.notificationMessage());
            if (!executedScheduler.isAsync()) {
                services.applicationEvent.emitEvent(ApplicationEvent.SCHEDULER_EXECUTED, executedScheduler);
            } else {
                services.applicationEvent.emitEventAsync(ApplicationEvent.SCHEDULER_EXECUTED, executedScheduler);
            }
        }

    }

}
