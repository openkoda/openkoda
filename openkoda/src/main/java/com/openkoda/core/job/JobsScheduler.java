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

import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import jakarta.inject.Inject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Jobs scheduling component.
 * Sets the frequency of main application jobs.
 * See also {@link EmailSenderJob}, {@link PostMessagesToWebhookJob}, {@link SearchIndexUpdaterJob}, {@link SystemHealthAlertJob}
 */
@Component
public class JobsScheduler implements LoggingComponentWithRequestId {

    @Inject EmailSenderJob emailSenderJob;
    @Inject PostMessagesToWebhookJob postMessagesToWebhookJob;
    @Inject SearchIndexUpdaterJob searchIndexUpdaterJob;
    @Inject SystemHealthAlertJob systemHealthAlertJob;

    @Scheduled(initialDelay = 10000, fixedDelay = 5000)
    public void emailSenderJob() {
        emailSenderJob.send();
    }

    @Scheduled(initialDelay = 10000, fixedDelay = 5000)
    public void postMessagesToWebhookJob() {
        postMessagesToWebhookJob.send();
    }

    @Scheduled(initialDelay = 10000, fixedDelay = 10000)
    public void searchIndexUpdaterJob() {
        searchIndexUpdaterJob.updateSearchIndexes();
    }

    @Scheduled(cron = "${scheduled.systemHealth.check:0 0 4 * * ?}")
    public void systemHealthAlertJob() {
        systemHealthAlertJob.checkSystem();
    }

}
