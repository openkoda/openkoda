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

package com.openkoda.core.service.event;

import com.openkoda.dto.NotificationDto;
import com.openkoda.dto.OrganizationDto;
import com.openkoda.dto.RegisteredUserDto;
import com.openkoda.dto.payment.PlanDto;
import com.openkoda.dto.system.ScheduledSchedulerDto;
import com.openkoda.dto.user.BasicUser;
import com.openkoda.dto.user.UserRoleDto;
import com.openkoda.model.event.EventListenerEntry;

import java.io.File;

/**
 * <p>ApplicationEvent class.</p>
 *
 * This class provides a convenient way to define different types of events that can occur in an application.
 * Each event is represented by an instance of ApplicationEvent<T>, which can be used to emit events and handle them appropriately.
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 * 
 */
public class ApplicationEvent<T> extends AbstractApplicationEvent<T> {

    public static final ApplicationEvent<BasicUser> USER_CREATED = new ApplicationEvent(BasicUser.class, "USER_CREATED");
    public static final ApplicationEvent<RegisteredUserDto> USER_REGISTERED = new ApplicationEvent(RegisteredUserDto.class, "USER_REGISTERED");
    public static final ApplicationEvent<BasicUser> USER_MODIFIED = new ApplicationEvent(BasicUser.class, "USER_MODIFIED");
    public static final ApplicationEvent<BasicUser> USER_DELETED = new ApplicationEvent(BasicUser.class, "USER_DELETED");
    public static final ApplicationEvent<BasicUser> USER_VERIFIED = new ApplicationEvent(BasicUser.class, "USER_VERIFIED");
    public static final ApplicationEvent<BasicUser> USER_LOGGED_IN = new ApplicationEvent(BasicUser.class, "USER_LOGGED_IN");
    public static final ApplicationEvent<OrganizationDto> ORGANIZATION_CREATED = new ApplicationEvent(OrganizationDto.class, "ORGANIZATION_CREATED");
    public static final ApplicationEvent<OrganizationDto> ORGANIZATION_MODIFIED = new ApplicationEvent(OrganizationDto.class, "ORGANIZATION_MODIFIED");
    public static final ApplicationEvent<OrganizationDto> ORGANIZATION_DELETED = new ApplicationEvent(OrganizationDto.class, "ORGANIZATION_DELETED");
    public static final ApplicationEvent<UserRoleDto> USER_ROLE_CREATED = new ApplicationEvent(UserRoleDto.class, "USER_ROLE_CREATED");
    public static final ApplicationEvent<UserRoleDto> USER_ROLE_MODIFIED = new ApplicationEvent(UserRoleDto.class, "USER_ROLE_MODIFIED");
    public static final ApplicationEvent<UserRoleDto> USER_ROLE_DELETED = new ApplicationEvent(UserRoleDto.class, "USER_ROLE_DELETED");
    public static final ApplicationEvent<EventListenerEntry> EVENT_LISTENER_CREATED = new ApplicationEvent(EventListenerEntry.class, "EVENT_LISTENER_CREATED");
    public static final ApplicationEvent<EventListenerEntry> EVENT_LISTENER_MODIFIED = new ApplicationEvent(EventListenerEntry.class, "EVENT_LISTENER_MODIFIED");
    public static final ApplicationEvent<EventListenerEntry> EVENT_LISTENER_DELETED = new ApplicationEvent(EventListenerEntry.class, "EVENT_LISTENER_DELETED");
    public static final ApplicationEvent<PlanDto> TRIAL_ACTIVATED = new ApplicationEvent<>(PlanDto.class, "TRIAL_ACTIVATED");
    public static final ApplicationEvent<PlanDto> TRIAL_EXPIRED = new ApplicationEvent<>(PlanDto.class, "TRIAL_EXPIRED");

    public static final ApplicationEvent<NotificationDto> APPLICATION_ERROR = new ApplicationEvent<>(NotificationDto.class, "APPLICATION_ERROR");

    public static ApplicationEvent<NotificationDto> NOTIFICATION_CREATED = new ApplicationEvent<>(NotificationDto.class, "NOTIFICATION_CREATED");
    /**
     * Event emitted in {@link com.openkoda.core.service.BackupService} when application backup is finished.
     * Emitted along with the tar backup file.
     */
    public static ApplicationEvent<File> BACKUP_CREATED = new ApplicationEvent(File.class, "BACKUP_CREATED");

    /**
     * Event emitted in {@link com.openkoda.core.service.BackupService} after successfully performing a secure copy of backup archive file.
     */
    public static final ApplicationEvent<String> BACKUP_FILE_COPIED = new ApplicationEvent<>(String.class, "BACKUP_FILE_COPIED");

    /**
     * This event is used when running {@link com.openkoda.model.event.Scheduler}.
     * The scheduled task emits SCHEDULER_EXECUTED event along with String parameter which invokes proper consumers
     * on the basis of the String value.
     */
    public static ApplicationEvent<ScheduledSchedulerDto> SCHEDULER_EXECUTED = new ApplicationEvent(ScheduledSchedulerDto.class, "SCHEDULER_EXECUTED");


    /**
     * Constructor of this class invoke the constructor of the superclass (AbstractApplicationEvent)
     * @see AbstractApplicationEvent
     */
    protected ApplicationEvent(Class<T> eventClass, String eventName) {
        super(eventClass, eventName);
    }

}
