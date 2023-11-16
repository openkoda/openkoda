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

package com.openkoda.controller.notification;

import com.openkoda.core.security.HasSecurityRules;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.openkoda.controller.common.URLConstants._HTML;

@Controller
@RequestMapping(_HTML)
public class NotificationController extends AbstractNotificationController implements HasSecurityRules {

    /**
     * <p>openAllOrganizationNotifications</p>
     * Gets all read and unread Notifications from repo as a NotificationKeeper object
     */
    @PreAuthorize(CHECK_IS_THIS_USERID)
    @RequestMapping(_NOTIFICATION + _USERID + _ALL)
    public Object openAllNotifications(@PathVariable(USERID) Long userId, @Qualifier("notification") Pageable notificationPageable) {
        debug("[openAllNotifications] UserId: {} ", userId);
        return getAllNotifications(userId, notificationPageable).mav("notification-all");
    }

 /**
     * <p>openAllOrganizationNotifications</p>
     * Gets all read and unread Notifications from repo as a NotificationKeeper object
     */
    @PreAuthorize(CHECK_IS_THIS_USERID)
    @RequestMapping(_ORGANIZATION_ORGANIZATIONID + _NOTIFICATION + _USERID + _ALL)
    public Object openAllNotifications(@PathVariable(ORGANIZATIONID) Long organizationId, @PathVariable(USERID) Long userId, @Qualifier("notification") Pageable notificationPageable) {
        debug("[openAllNotifications] UserId: {} OrgId: {}", userId, organizationId);
        return getAllNotifications(userId, organizationId, notificationPageable).mav("notification-all");
    }

    /**
     * <p>markNotificationAsRead</p>
     * Marks all visible Notifications in dropdown as read
     */
    @PreAuthorize(CHECK_IS_THIS_USERID)
    @PostMapping(value = {_ORGANIZATION_ORGANIZATIONID + _NOTIFICATION + _USERID + _MARK_READ, _NOTIFICATION + _USERID + _MARK_READ})
    public Object markNotificationAsRead(@PathVariable(name = ORGANIZATIONID, required = false) Long organizationId, @PathVariable(USERID) Long userId, @RequestParam("unreadNotifications") String unreadNotifications) {
        debug("[markNotificationAsRead] UserId: {} OrgId: {}", userId, organizationId);
        markAsRead(unreadNotifications, userId);
        return ResponseEntity.status(HttpStatus.OK).body("Successfully marked notifications as read!");
    }

    @PreAuthorize(CHECK_IS_THIS_USERID)
    @PostMapping(value = {_ORGANIZATION_ORGANIZATIONID + _NOTIFICATION + _USERID + _ALL + _MARK_READ, _NOTIFICATION + _USERID + _ALL + _MARK_READ})
    public Object markReadAllNotifications(@PathVariable(name = ORGANIZATIONID, required = false) Long organizationId, @PathVariable(USERID) Long userId) {
        debug("[markReadAllNotifications] UserId: {} OrgId: {}", userId, organizationId);
        markAllAsRead(userId, organizationId);
        return ResponseEntity.status(HttpStatus.OK).body("Successfully marked all user's notifications as read!");
    }
}
