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

import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import org.springframework.stereotype.Service;

import static com.openkoda.core.helper.ClusterHelper.*;
import static com.openkoda.core.service.event.ClusterEvent.EventType.*;

/**
 * Service that sends specific events to hazelcast cluster in order to propagate the local application state
 * to the whole cluster.
 * See: {@link ClusterEventListenerService}
 */
@Service
public class ClusterEventSenderService implements LoggingComponentWithRequestId {


    public boolean loadScheduler(long schedulerId) {
        debug("[loadScheduler] {}", schedulerId);
        if(isCluster()) {
            getHazelcastInstance().getTopic(CLUSTER_EVENT_TOPIC).publish(new ClusterEvent(SCHEDULER_ADD, schedulerId));
            return true;
        }
        return false;
    }

    public boolean reloadScheduler(long schedulerId) {
        debug("[reloadScheduler] {}", schedulerId);
        if(isCluster()) {
            getHazelcastInstance().getTopic(CLUSTER_EVENT_TOPIC).publish(new ClusterEvent(SCHEDULER_RELOAD, schedulerId));
            return true;
        }
        return false;
    }

    public boolean removeScheduler(long schedulerId) {
        debug("[removeScheduler] {}", schedulerId);
        if(isCluster()) {
            getHazelcastInstance().getTopic(CLUSTER_EVENT_TOPIC).publish(new ClusterEvent(SCHEDULER_REMOVE, schedulerId));
            return true;
        }
        return false;
    }

    public boolean loadEventListener(long eventListenerId) {
        debug("[loadEventListener] {}", eventListenerId);
        if(isCluster()) {
            getHazelcastInstance().getTopic(CLUSTER_EVENT_TOPIC).publish(new ClusterEvent(EVENT_LISTENER_ADD, eventListenerId));
            return true;
        }
        return false;
    }

    public boolean reloadEventListener(long eventListenerId) {
        debug("[reloadEventListener] {}", eventListenerId);
        if(isCluster()) {
            getHazelcastInstance().getTopic(CLUSTER_EVENT_TOPIC).publish(new ClusterEvent(EVENT_LISTENER_RELOAD, eventListenerId));
            return true;
        }
        return false;
    }

    public boolean removeEventListener(long eventListenerId) {
        debug("[removeEventListener] {}", eventListenerId);
        if(isCluster()) {
            getHazelcastInstance().getTopic(CLUSTER_EVENT_TOPIC).publish(new ClusterEvent(EVENT_LISTENER_REMOVE, eventListenerId));
            return true;
        }
        return false;
    }

    public boolean loadForm(long formId) {
        debug("[loadEventListener] {}", formId);
        if(isCluster()) {
            getHazelcastInstance().getTopic(CLUSTER_EVENT_TOPIC).publish(new ClusterEvent(FORM_ADD, formId));
            return true;
        }
        return false;
    }

    public boolean reloadForm(long formId) {
        debug("[reloadEventListener] {}", formId);
        if(isCluster()) {
            getHazelcastInstance().getTopic(CLUSTER_EVENT_TOPIC).publish(new ClusterEvent(FORM_RELOAD, formId));
            return true;
        }
        return false;
    }

    public boolean removeForm(long formId) {
        debug("[removeEventListener] {}", formId);
        if(isCluster()) {
            getHazelcastInstance().getTopic(CLUSTER_EVENT_TOPIC).publish(new ClusterEvent(FORM_REMOVE, formId));
            return true;
        }
        return false;
    }

}
