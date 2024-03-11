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

package com.openkoda.core.controller.event;

import com.openkoda.controller.ComponentProvider;
import com.openkoda.core.flow.Flow;
import com.openkoda.core.flow.PageModelMap;
import com.openkoda.core.helper.JsonHelper;
import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.core.service.event.AbstractApplicationEvent;
import com.openkoda.dto.NotificationDto;
import com.openkoda.dto.OrganizationDto;
import com.openkoda.dto.payment.InvoiceDto;
import com.openkoda.dto.payment.PaymentDto;
import com.openkoda.dto.payment.PlanDto;
import com.openkoda.dto.payment.SubscriptionDto;
import com.openkoda.dto.system.FrontendResourceDto;
import com.openkoda.dto.system.ScheduledSchedulerDto;
import com.openkoda.dto.user.BasicUser;
import com.openkoda.dto.user.UserRoleDto;
import com.openkoda.form.EventListenerForm;
import com.openkoda.form.FrontendMappingDefinitions;
import com.openkoda.form.SendEventForm;
import com.openkoda.model.component.event.Event;
import com.openkoda.model.component.event.EventListenerEntry;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.validation.BindingResult;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>Controller that provides actual {@link com.openkoda.core.service.event.EventListener} related functionality
 * for different types of access (eg. API, HTML)</p>
 * <p>Implementing classes should take over http binding and forming a result whereas this controller should take care
 * of actual implementation</p>
 *
 * <p>See also {@link EventListenerControllerHtml}, {@link EventListenerEntry}</p>
 *
 * @author Martyna Litkowska (mlitkowska@stratoflow.com)
 * @since 2019-03-11
 */
public class AbstractEventListenerController extends ComponentProvider implements HasSecurityRules {

    /**
     * Retrieves {@link EventListenerEntry} page from the database for the parameters provided
     *
     * @param eventListenerSearchTerm
     * @param eventListenerSpecification {@link Specification} for {@link EventListenerEntry} retrieval
     * @param eventListenerPageable {@link Pageable} for {@link EventListenerEntry} page search
     * @return com.openkoda.core.flow.PageModelMap
     */
    protected PageModelMap findListenersFlow(
            String eventListenerSearchTerm,
            Specification<EventListenerEntry> eventListenerSpecification,
            Pageable eventListenerPageable) {
        debug("[findListenersFlow]");
        return Flow.init()
                .thenSet(eventListenerPage, a -> repositories.secure.eventListener.search(eventListenerSearchTerm, null, eventListenerSpecification, eventListenerPageable))
                .execute();
    }

    /**
     * Prepares {@link EventListenerForm} for the {@link EventListenerEntry} retrieved from the database.
     *
     * @param organizationId
     * @param eListenerId
     * @return com.openkoda.core.flow.PageModelMap
     */
    protected PageModelMap find(Long organizationId, long eListenerId) {
        debug("[find] ListenerId: {}", eListenerId);
        return Flow.init()
                .thenSet(eventListenerEntity, a -> repositories.unsecure.eventListener.findOne(eListenerId))
                .thenSet(eventListenerForm, a -> new EventListenerForm(organizationId, a.result))
                .execute();
    }


    /**
     * Validates {@link EventListenerForm} data, populates to {@link EventListenerEntry} and saves new record in the database.
     * After successful save the {@link EventListenerEntry} is being registered as a listener.
     * See also {@link com.openkoda.core.service.event.EventListenerService}
     *
     * @param eListenerForm
     * @param br
     * @return com.openkoda.core.flow.PageModelMap
     */
    protected PageModelMap create(EventListenerForm eListenerForm, BindingResult br) {
        debug("[create]");
        return Flow.init(eventListenerForm, eListenerForm)
                .then(a -> services.validation.validateAndPopulateToEntity(eListenerForm, br,new EventListenerEntry()))
                .thenSet(eventListenerEntity, a -> repositories.unsecure.eventListener.save(a.result))
                .then(a -> services.componentExport.exportToFileIfRequired(a.result))
                .then(a -> services.eventListener.registerListenerClusterAware((EventListenerEntry) a.result))
                .thenSet(eventListenerForm, a -> new EventListenerForm())
                .execute();
    }

    /**
     * Retrieves {@link EventListenerEntry} from the database for the given ID.
     * Then validates {@link EventListenerForm} data, updates the existing {@link EventListenerEntry} entity and updates the database.
     * For a successful update it updates the active listener.
     * See also {@link com.openkoda.core.service.event.EventListenerService}
     *
     * @param eventListenerId
     * @param eListenerForm
     * @param br
     * @return com.openkoda.core.flow.PageModelMap
     */
    protected PageModelMap update(long eventListenerId, EventListenerForm eListenerForm, BindingResult br) {
        debug("[update] ListenerId: {}", eventListenerId);
        return Flow.init(eventListenerForm, eListenerForm)
                .then(a -> repositories.unsecure.eventListener.findOne(eventListenerId))
                .then(a -> services.validation.validateAndPopulateToEntity(eListenerForm, br, a.result))
                .then(a -> repositories.unsecure.eventListener.saveAndFlush(a.result))
                .then(a -> services.componentExport.exportToFileIfRequired(a.result))
                .then(a -> services.eventListener.updateEventListenerClusterAware((EventListenerEntry) a.result))
                .execute();
    }

    /**
     * Removes {@link EventListenerEntry} from the database for the given ID.
     * After successful removal the active listener is being unregistered from the app.
     * See also {@link com.openkoda.core.service.event.EventListenerService}, {@link EventListenerForm}
     *
     * @param eventListenerId
     * @return com.openkoda.core.flow.PageModelMap
     */
    protected PageModelMap remove(long eventListenerId) {
        debug("[remove] ListenerId: {}", eventListenerId);
        return Flow.init(eventListenerId)
                .thenSet(eventListenerEntityToUnregister, a -> repositories.unsecure.eventListener.findOne(eventListenerId))
                .then(a -> services.componentExport.removeExportedFilesIfRequired(a.result))
                .then(a -> repositories.unsecure.eventListener.deleteOne( a.result.getId() ) )
                .then(a -> services.eventListener.unregisterEventListenerClusterAware(a.model.get(eventListenerEntityToUnregister)))
                .execute();
    }


    /**
     * Prepares the {@link SendEventForm} for the manual event sending (available only in Admin panel)
     *
     * @return com.openkoda.core.flow.PageModelMap
     */
    protected PageModelMap chooseEvent() {
        debug("[chooseEvent]");
        return Flow.init()
                .thenSet(sendEventForm, a -> new SendEventForm())
                .execute();
    }

    /**
     * Prepares the {@link SendEventForm} for a particular {@link Event} class.
     * Meaning that the form returned by the method contains fields for the dto object of the selected event type.
     *
     * @param eventForm {@link SendEventForm}
     * @param br
     * @return com.openkoda.core.flow.PageModelMap
     */
    protected PageModelMap prepareEvent(SendEventForm eventForm, BindingResult br) {
        debug("[prepareEvent]");
        return Flow.init(sendEventForm, eventForm)
                .then(a -> services.validation.validate(eventForm, br))
                .then(a -> new Event(a.result.getEvent()))
                .thenSet(sendEventForm, a -> getEventFormForClass(a.result))
                .execute();
    }

    /**
     * Emits event triggered manually from the Admin panel.
     * Due to generic approach to this functionality, the event data is in the form of {@link Map} object.
     * See also {@link com.openkoda.core.service.event.ApplicationEventService}
     *
     * @param eventData map of event object parameters
     * @return com.openkoda.core.flow.PageModelMap
     * @throws IOException
     */
    protected PageModelMap emitEvent(Map<String, String> eventData) throws IOException {
        debug("[emitEvent]");
        Event event = new Event(eventData.remove("event"));
        Map<String, String> objectData = eventData.entrySet().stream()
                .filter(e -> e.getKey().startsWith("dto.") && StringUtils.isNotBlank(e.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        String json = JsonHelper.formMapToJson(objectData);
        json = "{" + "\"object@" + event.getEventObjectType() + "\":" + json + "}";
        Object object = JsonHelper.fromDebugJson(json).get("object");
        AbstractApplicationEvent appEvent = AbstractApplicationEvent.getEvent(event.getEventName());
        return Flow.init(sendEventForm, new SendEventForm(event.getEventString()))
                .then(a -> services.applicationEvent.emitEvent(appEvent, object))
                .execute();
    }

    /**
     * Prepares {@link SendEventForm} on the basis of {@link Event} class provided.
     * There is a different set of fields for each eventObjectType of the {@link Event}.
     * This is for the manual trigger of events in the Admin panel.
     *
     * @param event {@link Event}
     * @return com.openkoda.form.SendEventForm
     */
    private SendEventForm getEventFormForClass(Event event) {
        switch (event.getEventObjectType()){
            case "com.openkoda.dto.payment.InvoiceDto":
                return new SendEventForm<>(new InvoiceDto(), FrontendMappingDefinitions.sendEventInvoiceDto, event.getEventString());
            case "com.openkoda.dto.payment.PaymentDto":
                return new SendEventForm<>(new PaymentDto(), FrontendMappingDefinitions.sendEventPaymentDto, event.getEventString());
            case "com.openkoda.dto.payment.PlanDto":
                return new SendEventForm<>(new PlanDto(), FrontendMappingDefinitions.sendEventPlanDto, event.getEventString());
            case "com.openkoda.dto.payment.SubscriptionDto":
                return new SendEventForm<>(new SubscriptionDto(), FrontendMappingDefinitions.sendEventSubscriptionDto, event.getEventString());
            case "com.openkoda.dto.system.CmsDto":
                return new SendEventForm<>(new FrontendResourceDto(), FrontendMappingDefinitions.sendEventFrontendResourceDto, event.getEventString());
            case "com.openkoda.dto.system.ScheduledSchedulerDto":
                return new SendEventForm<>(new ScheduledSchedulerDto(), FrontendMappingDefinitions.sendEventScheduledSchedulerDto, event.getEventString());
            case "com.openkoda.dto.user.BasicUser":
                return new SendEventForm<>(new BasicUser(), FrontendMappingDefinitions.sendEventBasicUser, event.getEventString());
            case "com.openkoda.dto.user.UserRoleDto":
                return new SendEventForm<>(new UserRoleDto(), FrontendMappingDefinitions.sendEventUserRoleDto, event.getEventString());
            case "com.openkoda.dto.OrganizationDto":
                return new SendEventForm<>(new OrganizationDto(), FrontendMappingDefinitions.sendEventOrganizationDto, event.getEventString());
            case "com.openkoda.dto.NotificationDto":
                return new SendEventForm<>(new NotificationDto(), FrontendMappingDefinitions.sendEventNotificationDto, event.getEventString());
            default:
                return new SendEventForm();
        }
    }
}
