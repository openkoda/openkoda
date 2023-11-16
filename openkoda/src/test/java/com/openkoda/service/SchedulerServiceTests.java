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
import com.openkoda.core.service.event.SchedulerService;
import com.openkoda.model.event.Scheduler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * JUnits for {@link SchedulerService}
 *
 * @author Martyna Litkowska (mlitkowska@stratoflow.com)
 * @since 2019-03-21
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class SchedulerServiceTests extends AbstractTest {

    public static final String SCHEDULER_CRON_EXPRESSION = "1 * * * * *";
    public static final String SCHEDULER_EVENT_DATA = "data brought by event";

    @Spy
    @Qualifier("taskScheduler")
    private TaskScheduler taskScheduler;

    @InjectMocks
    private SchedulerService schedulerService;

    @Test
    public void scheduleAllFromDbTest() {
//        given
        List<Scheduler> schedulers = new ArrayList<>(0);
        mockAndAuthenticateUser(1l, "test@openkoda.com", "TEST", "(canManageBackend)");

//        when
        when(schedulerRepository.findAll()).thenReturn(schedulers);

//        then
        verify(taskScheduler, never()).schedule(any(SchedulerService.SchedulerTask.class), any(CronTrigger.class));
    }

    @Test
    public void scheduleNull() {
//        given
        Scheduler scheduler = null;
        mockAndAuthenticateUser(1l, "test@openkoda.com", "TEST", "(canManageBackend)");

//        when
        boolean result = schedulerService.schedule(scheduler);

//        then
        verify(taskScheduler, never()).schedule(any(SchedulerService.SchedulerTask.class), any(CronTrigger.class));
        assertFalse(result);
    }

    @Test
    public void scheduleNOTNull() {
//        given
        Scheduler scheduler = new Scheduler(SCHEDULER_CRON_EXPRESSION, SCHEDULER_EVENT_DATA, false);
        scheduler.setId(1L);
        mockAndAuthenticateUser(1l, "test@openkoda.com", "TEST", "(canManageBackend)");

//        when
        boolean result = schedulerService.schedule(scheduler);

//        then
        verify(taskScheduler).schedule(any(SchedulerService.SchedulerTask.class), any(CronTrigger.class));
        assertTrue(result);
    }

    @Test
    public void removeNonExisting() {
//        given
        long schedulerId = 1L;
        mockAndAuthenticateUser(1l, "test@openkoda.com", "TEST", "(canManageBackend)");

//        when
        boolean result = schedulerService.remove(schedulerId);

//        then
        assertFalse(result);
    }

    @Test
    public void removeScheduledUnsuccessful() {
//        given
        mockAndAuthenticateUser(1l, "test@openkoda.com", "TEST", "(canManageBackend)");
        Scheduler scheduler = new Scheduler(SCHEDULER_CRON_EXPRESSION, SCHEDULER_EVENT_DATA, false);
        long schedulerId = 1L;
        scheduler.setId(schedulerId);
        ScheduledFuture scheduledFuture = mock(ScheduledFuture.class);

//        when
        when(taskScheduler.schedule(any(SchedulerService.SchedulerTask.class), any(CronTrigger.class))).thenReturn(scheduledFuture);
        schedulerService.schedule(scheduler);
        when(scheduledFuture.cancel(anyBoolean())).thenReturn(false);
        boolean result = schedulerService.remove(schedulerId);

//        then
        assertFalse(result);
    }

    @Test
    public void removeScheduled() {
//        given
        mockAndAuthenticateUser(1l, "test@openkoda.com", "TEST", "(canManageBackend)");
        Scheduler scheduler = new Scheduler(SCHEDULER_CRON_EXPRESSION, SCHEDULER_EVENT_DATA, false);
        long schedulerId = 1L;
        scheduler.setId(schedulerId);
        ScheduledFuture scheduledFuture = mock(ScheduledFuture.class);

//        when
        when(taskScheduler.schedule(any(SchedulerService.SchedulerTask.class), any(CronTrigger.class))).thenReturn(scheduledFuture);
        schedulerService.schedule(scheduler);
        when(scheduledFuture.cancel(anyBoolean())).thenReturn(true);
        boolean result = schedulerService.remove(schedulerId);

//        then
        assertTrue(result);
    }

    @Test
    public void rescheduleNonExisting() {
//        given
        mockAndAuthenticateUser(1l, "test@openkoda.com", "TEST", "(canManageBackend)");
        Scheduler scheduler = new Scheduler(SCHEDULER_CRON_EXPRESSION, SCHEDULER_EVENT_DATA, false);
        long schedulerId = 1L;
        scheduler.setId(schedulerId);

//        when
        boolean result = schedulerService.reschedule(schedulerId, scheduler);

//        then
        assertFalse(result);
    }

    @Test
    public void rescheduleExistingUnsuccessful() {
//        given
        mockAndAuthenticateUser(1l, "test@openkoda.com", "TEST", "(canManageBackend)");
        Scheduler scheduler = new Scheduler(SCHEDULER_CRON_EXPRESSION, SCHEDULER_EVENT_DATA, false);
        long schedulerId = 1L;
        scheduler.setId(schedulerId);
        ScheduledFuture scheduledFuture = mock(ScheduledFuture.class);

//        when
        when(taskScheduler.schedule(any(SchedulerService.SchedulerTask.class), any(CronTrigger.class))).thenReturn(scheduledFuture);
        schedulerService.schedule(scheduler);
        when(scheduledFuture.cancel(anyBoolean())).thenReturn(false);
        boolean result = schedulerService.reschedule(schedulerId, scheduler);

//        then
        assertFalse(result);
    }

    @Test
    public void rescheduleExisting() {
//        given
        mockAndAuthenticateUser(1l, "test@openkoda.com", "TEST", "(canManageBackend)");
        Scheduler scheduler = new Scheduler(SCHEDULER_CRON_EXPRESSION, SCHEDULER_EVENT_DATA, false);
        long schedulerId = 1L;
        scheduler.setId(schedulerId);
        ScheduledFuture scheduledFuture = mock(ScheduledFuture.class);

//        when
        when(taskScheduler.schedule(any(SchedulerService.SchedulerTask.class), any(CronTrigger.class))).thenReturn(scheduledFuture);
        schedulerService.schedule(scheduler);

        when(scheduledFuture.cancel(anyBoolean())).thenReturn(true);
        boolean result = schedulerService.reschedule(schedulerId, scheduler);

//        then
        assertTrue(result);
    }
}
