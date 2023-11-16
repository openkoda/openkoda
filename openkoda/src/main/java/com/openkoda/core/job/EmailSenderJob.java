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

import com.openkoda.core.service.email.EmailSender;
import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import com.openkoda.model.task.Email;
import com.openkoda.repository.task.EmailRepository;
import com.openkoda.repository.task.TaskRepository;
import jakarta.inject.Inject;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *  Job sending {@link Email}.
 *  See also {@link EmailSender}, {@link EmailRepository}
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 *
 */
@Component
public class EmailSenderJob  implements LoggingComponentWithRequestId {

    @Inject
    EmailSender emailSender;

    @Inject
    EmailRepository emailRepository;

    @Transactional
    public void send() {
        trace("[send email job]");
        Page<Email> emails = emailRepository.findTasksAndSetStateDoing( () -> emailRepository.findByCanBeStartedTrue(TaskRepository.OLDEST_10) );
        for (Email e : emails.getContent()) {
            //as it's a separate transaction, we need to re-read the email
            Email e2 = emailRepository.findById(e.getId()).get();
            emailSender.sendMail(e2);
            emailRepository.save(e2);
        }
    }

}
