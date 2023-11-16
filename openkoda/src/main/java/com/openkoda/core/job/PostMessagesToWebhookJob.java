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

import com.openkoda.core.service.RestClientService;
import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import com.openkoda.model.task.HttpRequestTask;
import com.openkoda.repository.task.HttpRequestTaskRepository;
import com.openkoda.repository.task.TaskRepository;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

/**
 * Job posting messages ({@link HttpRequestTask}) to the assigned webhook URL.
 * See also {@link HttpRequestTaskRepository}, {@link RestTemplate}
 *
 * @author Martyna Litkowska (mlitkowska@stratoflow.com)
 * @since 2019-07-02
 */
@Component
public class PostMessagesToWebhookJob implements LoggingComponentWithRequestId {

    @Inject
    private HttpRequestTaskRepository httpRequestTaskRepository;

    private RestTemplate restTemplate;

    @PostConstruct
    private void init() {
        debug("[init] Preparing RestTemplate object with headers");
        restTemplate = new RestTemplate();
    }

    /**
     * Retrieves {@link HttpRequestTask} from the database which are ready to be sent.
     * It prepares the HTTP request for each of them and sends as a POST request to the webhook URL assigned to each entry.
     * Finally, it updates the status of a {@link HttpRequestTask}.
     * See also {@link HttpRequestTaskRepository}, {@link RestTemplate}
     */
    @Transactional
    public void send() {
        trace("[send] to Webhook");

        // Retrieve the oldest 100 HttpRequestObjects stored in database to be sent (date sent must be null)
        Page<HttpRequestTask> httpRequestsToSend = httpRequestTaskRepository.findTasksAndSetStateDoing(
                () -> httpRequestTaskRepository.findByCanBeStartedTrue(TaskRepository.OLDEST_100) );

        httpRequestsToSend.forEach(httpRequestTask -> {
            httpRequestTask.start();
            httpRequestTaskRepository.save(httpRequestTask);
            HttpHeaders httpHeaders = RestClientService.prepareHttpHeaders(httpRequestTask);
            HttpEntity<String> httpEntity = new HttpEntity<>(httpRequestTask.getJson(), httpHeaders);
            ResponseEntity<String> webhookResponse = restTemplate.postForEntity(httpRequestTask.getRequestUrl(), httpEntity, String.class);
            //FIXME: [adrysch] why do we check for 'ok' or '1'?
            //if (webhookResponse.getStatusCode().equals(HttpStatus.OK) && (webhookResponse.getBody().equals("ok") || webhookResponse.getBody().equals("1"))) {
            if (webhookResponse.getStatusCode().equals(HttpStatus.OK)) {
                httpRequestTask.setDateSent(new Date());
                httpRequestTask.complete();
            } else {
                httpRequestTask.fail();
                error("Notification couldn't have been sent due to {} {}", webhookResponse.getStatusCodeValue(), webhookResponse.getBody());
            }
            httpRequestTaskRepository.save(httpRequestTask);
        });
    }
}
