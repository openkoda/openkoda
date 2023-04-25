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

import com.hazelcast.topic.Message;
import com.hazelcast.topic.MessageListener;
import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import jakarta.inject.Inject;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * Listener service listening to Hazelcast messages to propagate state changes over the application cluster.
 * It is only used in 'hazelcast' profile.
 * See {@link ClusterEventSenderService}
 */
@Service
public class ClusterEventListenerService implements MessageListener<ClusterEvent>, LoggingComponentWithRequestId {

    @Inject @Lazy
    private SchedulerService schedulerService;

    @Inject @Lazy
    private EventListenerService eventListenerService;

    @Override
    //TODO Rule 2.1: public method must not return void - it's implementation of an interface so can't change the signature of the method
    public void onMessage(Message<ClusterEvent> message) {
        ClusterEvent m = message.getMessageObject();
        debug("[onMessage] {} {}", m.eventType, m.id);
        switch (m.eventType) {
            case SCHEDULER_ADD: schedulerService.loadFromDb(m.id); break;
            case SCHEDULER_REMOVE: schedulerService.remove(m.id); break;
            case SCHEDULER_RELOAD: schedulerService.removeAndLoadFromDb(m.id); break;
            case EVENT_LISTENER_ADD: eventListenerService.loadFromDb(m.id); break;
            case EVENT_LISTENER_REMOVE: eventListenerService.unregisterEventListener(m.id); break;
            case EVENT_LISTENER_RELOAD: eventListenerService.removeAndLoadFromDb(m.id); break;
        }

    }


}
