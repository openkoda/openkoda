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

import com.openkoda.controller.ComponentProvider;
import com.openkoda.core.helper.ClusterHelper;
import com.openkoda.core.helper.NameHelper;
import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.model.component.event.Consumer;
import com.openkoda.model.component.event.EventListenerEntry;
import jakarta.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Arrays;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;

import static com.openkoda.core.service.event.ApplicationEvent.*;

/**
 * @author Martyna Litkowska (mlitkowska@stratoflow.com)
 * @since 2019-03-11
 */
@DependsOn({"allServices", "applicationEventService"})
@Service
public class EventListenerService extends ComponentProvider implements HasSecurityRules {

    private Map<Object, String> events = new LinkedHashMap<>();
    private Map<Object, String> consumers = new LinkedHashMap<>();
    private Map<Object, Map<String, String>> consumersArray = new LinkedHashMap<>();

    private List<Class> eventClasses = new ArrayList<>();

    @Inject
    private ClusterEventSenderService clusterEventSenderService;



    /**
     * Returns map of events available to assign to listeners
     * Object is a String of event (EventClassName, EventName, EventObjectType)
     * String is a name to display in UI in dropdown
     *
     * @return Map<Object, String>
     */
    public Map<Object, String> getEvents() {
        return events;
    }

    /**
     * Returns map of consumers available to assign to listeners
     * Object is a String of consumer (ConsumerClassName, ConsumerMethodName, ConsumerObjectType, IfConsumerHasMorePrams)
     * String is a name to display in UI in dropdown
     *
     * @return Map<Object, String>
     */
    public Map<Object, String> getConsumers() {
        return consumers;
    }


    /**
     * Method that register the classes of events.
     * @param events  array of Class objects
     * @param <T>
     * @return a boolean value indicating whether the set was changed as a result of the operation.
     */
    public <T> boolean registerEventClasses(Class<T>[] events) {
        debug("[registerEventClasses]");
        return eventClasses.addAll(java.util.Arrays.asList(events));
    }

    /**
     * Method that register a single event class.
     * @param eventClass
     * @param <T>
     * @return a boolean value indicating whether the event class was successfully added to the collection.
     */
    public <T> boolean registerEventClass(Class<T> eventClass) {
        debug("[registerEventClass]");
        return eventClasses.add(eventClass);
    }

    /**
     * This method registers all event listeners that are stored in a database.
     * @see EventListenerService#registerListener(EventListenerEntry)
     * @return true to indicate that all event listeners were successfully registered.
     *
     */
    public boolean registerAllEventListenersFromDb() {
        debug("[registerAllEventListenersFromDb]");
        repositories.unsecure.eventListener.findAll().forEach(this::registerListener);
        return true;
    }

    /**
     * This method sets all available application events
     * @return a map of all available application events
     */
    public Map<Object, String> setAllAvailableAppEvents() {
        debug("[setAllAvailableAppEvents]");
        for (Class<AbstractApplicationEvent> ec : eventClasses) {
            for (Field field : ec.getFields()) {
                String eventType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0].getTypeName();
                events.put(
                        StringUtils.join(Arrays.array(
                                field.getType().getName(),
                                field.getName(),
                                eventType),
                                ","),
                        field.getName() + " (" + NameHelper.getClassName(eventType) + ")");
            }
        }
        return events;
    }

    /**
     * This method sets all available application consumers
     */
    public Map<Object, String> setAllAvailableAppConsumers() {
        debug("[setAllAvailableAppConsumers]");
        Set<Map.Entry<Class, List<EventConsumer>>> consumersMap = services.applicationEvent.getConsumertEntrySet();
        if (consumersMap != null) {
            for (Map.Entry<Class, List<EventConsumer>> c : consumersMap) {
                for (EventConsumer ec : c.getValue()) {
                    if (ec.getConsumerMethod() != null) {
                        String canonicalName = Consumer.canonicalMethodName(
                                ec.getConsumerMethod().getDeclaringClass().getName(),
                                ec.getConsumerMethod().getName(),
                                ec.getConsumerMethod().getParameterTypes()[0].getName(),
                                ec.getConsumerMethod().getParameterTypes().length - 1);
                        consumers.put(canonicalName, ec.getDescription());
                        consumersArray.put(canonicalName, ec.propertiesToMap());
                    }
                }
            }
        }
        return consumers;
    }

    /**
     * This method retrieves an instance of an AbstractApplicationEvent subclass by its class name and field name.
     * @param className
     * @param fieldName
     * @return
     *  If the field cannot be found or the casting fails, this method will throw
     * @throws ClassNotFoundException
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private AbstractApplicationEvent getEventByClassAndName(String className, String fieldName) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        debug("Getting event by class: {} and name: {}", className, fieldName);
        Field field = Class.forName(className).getField(fieldName);
        return (AbstractApplicationEvent) field.get(this);
    }


    /**
     * @param eventObjectClass
     * @param consumerClassName
     * @param consumerMethodName
     * @param numberOfParameters
     * @return EventConsumer
     */
    private EventConsumer getConsumer(Class eventObjectClass, String consumerClassName, String consumerMethodName, int numberOfParameters) {
        debug("[getConsumer] event object class: {}, consumer class name: {}, consumer method name: {}, number of static method parameters: {}",
                eventObjectClass.getName(), consumerClassName, consumerMethodName, numberOfParameters);
        List<EventConsumer> eventConsumers = services.applicationEvent.findConsumersByEventType(eventObjectClass);
        for (EventConsumer ec : eventConsumers) {
            if (ec.verifyMethod(consumerClassName, consumerMethodName, eventObjectClass, numberOfParameters)) {
                return ec;
            }
        }
        return null;
    }

    /**
     * This method register an event listener and emits an application event EVENT_LISTENER_CREATED
     * to notify other parts of the application about the creation of the event listener
     * @see ApplicationEvent#EVENT_LISTENER_CREATED
     * @see EventListenerService#registerListener(EventListenerEntry)
     * @param eventListenerEntry
     * @return
     */
    @PreAuthorize(CHECK_CAN_MANAGE_EVENT_LISTENERS)
    public boolean registerListenerClusterAware(EventListenerEntry eventListenerEntry) {
        debug("[registerListenerClusterAware] {}", eventListenerEntry);
        boolean result;
        if (ClusterHelper.isCluster()) {
            result = clusterEventSenderService.loadEventListener(eventListenerEntry.getId());
        } else {
            result = registerListener(eventListenerEntry);
        }
        services.applicationEvent.emitEvent(EVENT_LISTENER_CREATED, eventListenerEntry);
        return result;
    }

    /**
     * This method unregister an event listener and emits an application event EVENT_LISTENER_DELETED
     * to notify other parts of the application about the deletion of the event listener
     * @see ApplicationEvent#EVENT_LISTENER_DELETED
     * @see EventListenerService#unregisterEventListener(Long)
     * @param eventListenerEntry
     * @return the result of the unregister operation.
     */
    @PreAuthorize(CHECK_CAN_MANAGE_EVENT_LISTENERS)
    public boolean unregisterEventListenerClusterAware(EventListenerEntry eventListenerEntry) {
        debug("[updateEventListenerClusterAware] {}", eventListenerEntry);
        boolean result;
        if (ClusterHelper.isCluster()) {
            result = clusterEventSenderService.removeEventListener(eventListenerEntry.getId());
        } else {
            result = unregisterEventListener(eventListenerEntry.getId());
        }
        services.applicationEvent.emitEvent(EVENT_LISTENER_DELETED, eventListenerEntry);
        return result;
    }


    /**
     * This method updates existing event listener.
     * @param eventListenerEntry
     * @return
     */
    @PreAuthorize(CHECK_CAN_MANAGE_EVENT_LISTENERS)
    public boolean updateEventListenerClusterAware(EventListenerEntry eventListenerEntry) {
        debug("[updateEventListenerClusterAware] {}", eventListenerEntry);
        boolean result;
        if (ClusterHelper.isCluster()) {
            result = clusterEventSenderService.reloadEventListener(eventListenerEntry.getId());
        } else {
            result = updateEventListener(eventListenerEntry);
        }
        return result;
    }

    /**
     * Method updates the corresponding event listener by unregistering and register event listener
     * and emits an application event EVENT_LISTENER_MODIFIED
     * @see EventListenerService#registerListener(EventListenerEntry)
     * @see EventListenerService#unregisterEventListener(Long)
     * @return a boolean value indicating whether the listener was successfully updated.
     */
    private boolean updateEventListener(EventListenerEntry eventListenerEntry) {
        debug("[updateEventListener] {}", eventListenerEntry);
        unregisterEventListener(eventListenerEntry.getId());
        boolean r = registerListener(eventListenerEntry);
        services.applicationEvent.emitEvent(EVENT_LISTENER_MODIFIED, eventListenerEntry);
        return r;
    }

    /**
     * This method unregister event listener and call loadFromDb method.
     * @see EventListenerService#unregisterEventListener(Long)
     * @param eventListenerEntryId
     * @return
     */
    public boolean removeAndLoadFromDb(Long eventListenerEntryId) {
        debug("[removeAndLoadFromDb] eventListenerEntryId: {}", eventListenerEntryId);
        unregisterEventListener(eventListenerEntryId);
        return loadFromDb(eventListenerEntryId);
    }

    /**
     * This method loads an event listener from the database by its ID and registers it in the application as a listener.
     * @see EventListenerService#registerListener(EventListenerEntry)
     * @return
     */
    public boolean loadFromDb(Long eventListenerEntryId) {
        debug("[loadFromDb] eventListenerEntryId: {}", eventListenerEntryId);
        EventListenerEntry eventListenerEntry = repositories.unsecure.eventListener.findOne(eventListenerEntryId);
        return registerListener(eventListenerEntry);
    }

    /**
     * This method register listener.
     *
     * @param eventListenerEntry
     * @return
     */
    private boolean registerListener(EventListenerEntry eventListenerEntry) {
        debug("[registerListener] {}", eventListenerEntry);
        try {
            Class<?> eventObjectClass = Class.forName(eventListenerEntry.getEventObjectType());

            int numberOfConsumerMethodParameters = getNumberOfConsumerMethodParameters(eventListenerEntry);

            EventConsumer consumer = getConsumer(
                    eventObjectClass,
                    eventListenerEntry.getConsumerClassName(),
                    eventListenerEntry.getConsumerMethodName(),
                    numberOfConsumerMethodParameters
            );
            if (consumer != null) {
                info("Registering event listener {}", eventListenerEntry);
                services.applicationEvent.registerEventListener(
                        getEventByClassAndName(eventListenerEntry.getEventClassName(), eventListenerEntry.getEventName()),
                        consumer,
                        eventListenerEntry.getStaticData1(),
                        eventListenerEntry.getStaticData2(),
                        eventListenerEntry.getStaticData3(),
                        eventListenerEntry.getStaticData4(),
                        eventListenerEntry.getId());
                return true;
            } else {
                warn("Event Listener not registered {}", eventListenerEntry);
                throw new RuntimeException(formatMessage("Event Listener not registered {}", eventListenerEntry));
            }
        } catch (Exception e) {
            error(e, "Could not register event listener {}", eventListenerEntry);
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @return an integer value indicating the number of non-null static data fields in the object.
     */
    private int getNumberOfConsumerMethodParameters(EventListenerEntry listenerEntry) {
        int numberOfParameters = 0;

        if (listenerEntry.getStaticData1() != null) { numberOfParameters++; }
        if (listenerEntry.getStaticData2() != null) { numberOfParameters++; }
        if (listenerEntry.getStaticData3() != null) { numberOfParameters++; }
        if (listenerEntry.getStaticData4() != null) { numberOfParameters++; }
        return numberOfParameters;
    }


    /**
     * This method is responsible for unregistering an event listener by its ID
     * @param eventListenerEntryId
     * @returnif unregistering successful method returns true otherwise, it logs an error message and returns false.
     */
    public boolean unregisterEventListener(Long eventListenerEntryId) {
        debug("[unregisterEventListener] eventListenerEntryId: {}", eventListenerEntryId);
        try {
            return services.applicationEvent.unregisterEventListener(eventListenerEntryId);
        } catch (Exception e) {
            error(e, "Could not register event listener {}", eventListenerEntryId);
        }
        return false;
    }

    public Map<Object, Map<String, String>> getConsumersArray() {
        return consumersArray;
    }
}
