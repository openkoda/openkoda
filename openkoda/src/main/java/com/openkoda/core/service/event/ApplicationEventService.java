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
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;
import reactor.util.function.Tuple6;
import reactor.util.function.Tuples;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * This class manages events and event listeners. It provides methods to register event listeners and consumers for specific events.
 */
@Service("applicationEventService")
    public class ApplicationEventService implements LoggingComponentWithRequestId {

    public class ListenerTupleList extends ArrayList<Tuple6<EventConsumer, String, String, String, String, Long>>{}

    private final ListenerTupleList empty = new ListenerTupleList();

    private static ApplicationEventService thisService;

    private final static ExecutorService asyncEventsExecutor = Executors.newFixedThreadPool(4);

    public Map<AbstractApplicationEvent,
            ListenerTupleList> listeners = new HashMap<>();

    private LinkedHashMap<Class, List<EventConsumer>> consumers = new LinkedHashMap<>();

    /**
     * This is a synchronized method that registers an event listener for a specific event.
     *
     * @return true if the tuple was successfully added
     */
    synchronized public <T> boolean registerEventListener(AbstractApplicationEvent<T> event, EventConsumer<T> eventConsumer, String staticData1, String staticData2, String staticData3, String staticData4, Long eventListenerId) {
        debug("[registerEventListener] event: {} eventConsumer: {} eventListenerId: {}", event, eventConsumer, eventListenerId);
        ListenerTupleList eventListeners = getEventListener(event);
        return eventListeners.add(Tuples.of(eventConsumer, staticData1, staticData2, staticData3, staticData4, eventListenerId));
    }

    /**
     * This is a synchronized method that registers an event listener for a specific event.
     *
     * @return a boolean value indicating whether the listener was successfully added to the list.
     */
    synchronized public <T> boolean registerEventListener(AbstractApplicationEvent<T> event, Consumer<T>
            eventListener) {
        debug("[registerEventListener] event: {} eventListener: {}", event, eventListener);
        ListenerTupleList eventListeners = getEventListener(event);
        return eventListeners.add(Tuples.of(new EventConsumer(eventListener), null, null, null, null, null));
    }

    /**
     * This is a synchronized method that registers an event listener for a specific event.
     *
     * @return a boolean value indicating whether the listener was successfully added to the list.
     */
    synchronized public <T> boolean registerEventListener(AbstractApplicationEvent<T> event, BiConsumer<T, String>
            eventListener, String staticData1, String staticData2, String staticData3, String staticData4) {
        debug("[registerEventListener] event: {}", event);
        ListenerTupleList eventListeners = getEventListener(event);
        return eventListeners.add(Tuples.of(new EventConsumer(eventListener), staticData1, staticData2, staticData3, staticData4, null));
    }


    /**
     * @return the corresponding ListenerTupleList object from the listeners map.
     * If the map does not already contain the specified event, a new ListenerTupleList is created and added to the map.
     * This ensures that there is always a ListenerTupleList associated with every event, even if no listeners are registered for that event yet.
     */
    private <T> ListenerTupleList getEventListener(AbstractApplicationEvent<T> event) {
        debug("[getEventListener] event: {}", event);
        ListenerTupleList eventListeners = listeners.get(event);
        if (eventListeners == null) {
            eventListeners = new ListenerTupleList();
            listeners.put(event, eventListeners);
        }
        return eventListeners;
    }

    /**
     *
     * This method allows registering an event consumer for a given event class.
     *
     * @param eventClass specifies the class of the event that the consumer should handle
     * @param eventConsumer is the consumer object that will handle the event
     * @returna boolean indicating whether the addition was successful.
     */
    synchronized public <T> boolean registerEventConsumer(Class<T> eventClass, EventConsumer<T> eventConsumer) {
        debug("[registerEventListener] eventConsumer: {}", eventConsumer);
        List<EventConsumer> eventConsumers = consumers.get(eventClass);
        if (eventConsumers == null) {
            eventConsumers = new ArrayList<>();
            consumers.put(eventClass, eventConsumers);
        }
        return eventConsumers.add(eventConsumer);
    }

    /**
     * This method registers an event consumer for a given event class with a specified method of the consumer class that will handle the event.
     *
     * @param eventClass
     * @param eventConsumerClass
     * @param eventConsumerMethodName
     * @param description
     * @param methodStaticParamsClass
     * @param <T>
     * @return
     * @throws NoSuchMethodException
     */
    synchronized public <T> boolean registerEventConsumerWithMethod(Class<T> eventClass,
                                                                    Class eventConsumerClass,
                                                                    String eventConsumerMethodName,
                                                                    String description,
                                                                    Class... methodStaticParamsClass) {
        debug("[registerEventConsumerWithMethod] methodName: {} description: {}", eventConsumerMethodName, description);
        try {
            EventConsumer<T> eventConsumer;

            if (methodStaticParamsClass.length == 0) {
                eventConsumer = new EventConsumer<>(eventConsumerClass.getMethod(eventConsumerMethodName, eventClass), eventClass, 0, description);
            } else {
                Class[] eventClassInArray = {eventClass};
                Class[] allMethodParamsClass = ArrayUtils.addAll(eventClassInArray, methodStaticParamsClass);
                eventConsumer = new EventConsumer<>(eventConsumerClass.getMethod(eventConsumerMethodName, allMethodParamsClass), eventClass, methodStaticParamsClass.length, description);
            }

            info("Registering Event Consumer {} with method {} for event type {}. {}", eventConsumerClass, eventConsumerMethodName, eventClass, description);
            return registerEventConsumer(eventClass, eventConsumer);
        } catch (NoSuchMethodException e) {
            error(e, "Could not find event consumer method [{}, {}]. Consumer not registered.", eventConsumerClass.getName(), eventConsumerMethodName);
        }
        return false;
    }

    /**
     * This method unregisters an event listener identified by the provided eventListenerEntryId.
     * It searches through all the registered listeners for the given eventListenerEntryId and removes it from the list.
     * @param eventListenerEntryId
     * @param <T>
     * @return a boolean indicating whether the unregistering was successful.
     */
    synchronized public <T> boolean unregisterEventListener(Long eventListenerEntryId) {
        debug("[unregisterEventListener] eventListenerEntryId: {}", eventListenerEntryId);
        Optional<Tuple6<EventConsumer, String, String, String, String, Long>> listenerTuple = null;
        for (Map.Entry<AbstractApplicationEvent, ListenerTupleList> l : listeners.entrySet()) {
            ListenerTupleList t = l.getValue();
            listenerTuple = t.stream()
                    .filter(tuple -> tuple.getT6() != null && tuple.getT6().equals(eventListenerEntryId))
                    .findFirst();
            if (listenerTuple.isPresent()) {
                return t.remove(listenerTuple.get());
            }
        }
        debug("[unregisterEventListener] no eventListener with entryId {} found", eventListenerEntryId);
        return false;
    }

    /**
     * This method is used to emit an event asynchronously.
     * @param event
     * @param object
     * @param <T>
     * @return true to indicate that the event was submitted for processing.
     */
    public <T> boolean emitEventAsync(AbstractApplicationEvent<T> event, T object) {
        asyncEventsExecutor.submit(() -> emitEvent(event, object));
        return true;
    }

    /**
     * This emitEvent method is responsible for triggering an event and calling the associated event consumers.
     * @param event
     * @param object
     * @param <T>
     * @return true to indicate that the event was successfully emitted.
     */
    public <T> boolean emitEvent(AbstractApplicationEvent<T> event, T object) {
        debug("[emitEvent] event: {}", event);
        listeners.getOrDefault(event, empty).forEach(
                a -> {
                    if (a.getT2() == null) {
                        a.getT1().accept(object, null);
                    } else if (a.getT3() == null) {
                        a.getT1().accept(object, a.getT2());
                    } else if (a.getT4() == null) {
                        a.getT1().accept(object, a.getT2(), a.getT3());
                    } else if (a.getT5() == null) {
                        a.getT1().accept(object, a.getT2(), a.getT3(), a.getT4());
                    } else {
                        a.getT1().accept(object, a.getT2(), a.getT3(), a.getT4(), a.getT5());
                    }
                });
        return true;
    }

    /**
     * @return a set view of the mappings contained in the consumers map,
     * where each mapping is a key-value pair consisting of a Class object as the key and a List of EventConsumer objects as the value.
     */
    Set<Map.Entry<Class, List<EventConsumer>>> getConsumertEntrySet() {
        return consumers.entrySet();
    }

    /**
     *
     * @param c
     * @return a list of EventConsumer objects
     */
    List<EventConsumer> findConsumersByEventType(Class c) {
        debug("[findConsumersByEventType] type: {}", c);
        List<EventConsumer> result = new ArrayList<>();
        for (Map.Entry<Class, List<EventConsumer>> e : consumers.entrySet()) {
            if (e.getKey().isAssignableFrom(c)) {
                result.addAll(e.getValue());
            }
        }
        return result;
    }

    /**
     * Method executed automatically after the bean has been constructed by the Spring framework,
     * and sets service to thisService
     */
    @PostConstruct
    void setThisService() {
        if (thisService == null) {
            thisService = this;
        }
    }

    /**
     * @return ApplicationEventService
     */
    public static ApplicationEventService getApplicationEventService() {
        return thisService;
    }

}
