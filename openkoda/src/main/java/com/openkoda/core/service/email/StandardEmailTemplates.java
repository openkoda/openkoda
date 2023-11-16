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

package com.openkoda.core.service.email;


/**
 *  Standards name Thymeleaf templates
 */

public interface StandardEmailTemplates {
    String EMAIL_BASE = "frontend-resource/email/";
    String INVITE_EXISTING = EMAIL_BASE + "invite-existing";
    String INVITE_NEW = EMAIL_BASE + "invite-new";
    String NEW_INVOICE_EMAIL = EMAIL_BASE + "new-invoice-email";
    String NOTIFICATION_ORGANIZATION_EMAIL = EMAIL_BASE + "notification-organization-email";
    String NOTIFICATION_USER_EMAIL = EMAIL_BASE + "notification-user-email";
    String PASSWORD_RECOVERY = EMAIL_BASE + "password-recovery";
    String PAYMENT_FAILED_EMAIL = EMAIL_BASE + "payment-failed-email";
    String PAYMENT_IN_14_DAYS = EMAIL_BASE + "payment-in-14-days";
    String SUBSCRIPTION_CONFIRMED = EMAIL_BASE + "subscription-confirmed";
    String TRIAL_EXPIRED_EMAIL = EMAIL_BASE + "trial-expired-email";
    String TRIAL_WILL_EXPIRE_EMAIL = EMAIL_BASE + "trial-will-expire-email";
    String WELCOME = EMAIL_BASE + "welcome";
}
