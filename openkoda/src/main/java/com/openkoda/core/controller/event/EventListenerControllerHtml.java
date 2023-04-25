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

package com.openkoda.core.controller.event;

import com.openkoda.form.EventListenerForm;
import com.openkoda.form.SendEventForm;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

import static com.openkoda.core.controller.generic.AbstractController.*;

/**
 *  <p>The controller for server-side generated html actions extending the {@link AbstractEventListenerController} which
 *  does the actual logic.</p>
 *  <p>General contract is: resolve HTTP bindings, delegate work to {@link AbstractEventListenerController} and provide
 *  ModelAndView</p>
 *  See also {@link AbstractEventListenerController}
 *
 * @author Martyna Litkowska (mlitkowska@stratoflow.com)
 * @since 2019-03-11
 */
@RestController
@RequestMapping(_HTML + _EVENTLISTENER)
public class EventListenerControllerHtml extends AbstractEventListenerController {

    /**
     * Prepares model and view to display all {@link com.openkoda.model.event.EventListenerEntry} page
     * See also {@link AbstractEventListenerController}
     *
     * @param pageable
     * @param search
     * @return java.lang.Object
     */
    @PreAuthorize(CHECK_CAN_READ_BACKEND)
    @GetMapping(value = _ALL)
    public Object getAll(
            @Qualifier("event") Pageable pageable,
            @RequestParam(required = false, defaultValue = "", name = "event_search") String search) {
        debug("[getAll]");
        return findListenersFlow(search, null, pageable)
                .mav(EVENTLISTENER + "-" + ALL);
    }

    /**
     * Prepares model and view for {@link com.openkoda.model.event.EventListenerEntry} settings page
     * See also {@link AbstractEventListenerController}
     *
     * @param eListenerId
     * @return java.lang.Object
     */
    @PreAuthorize(CHECK_CAN_READ_BACKEND)
    @GetMapping(value = _ID + _SETTINGS)
    public Object settings(@PathVariable(ID) Long eListenerId)
    {debug("[settings] ListenerId: {}", eListenerId);
        return find(null, eListenerId)
                .mav(EVENTLISTENER + "-settings");
    }

    /**
     * Prepares model and view for the new {@link com.openkoda.model.event.EventListenerEntry} configuration page
     * See also {@link AbstractEventListenerController}
     *
     * @return java.lang.Object
     */
    @PreAuthorize(CHECK_CAN_MANAGE_BACKEND)
    @GetMapping(_NEW_SETTINGS)
    public Object newListener() {
        debug("[newListener]");
        return find(null, -1L)
                .mav(EVENTLISTENER + "-settings");
    }

    /**
     * Triggers update of the {@link com.openkoda.model.event.EventListenerEntry} and prepares model and view for the result page
     * See also {@link AbstractEventListenerController}
     *
     * @param listenerId
     * @param eventListenerForm
     * @param br
     * @return java.lang.Object
     */
    @PreAuthorize(CHECK_CAN_MANAGE_BACKEND)
    @PostMapping(value = _ID + _SETTINGS)
    public Object updateEventListener(@PathVariable(ID) Long listenerId, @Valid EventListenerForm eventListenerForm, BindingResult br) {
        debug("[updateEventListener] ListenerId: {}", listenerId);
        return update(listenerId, eventListenerForm, br)
                .mav(ENTITY + '-' + FORMS + "::eventlistener-settings-form-success",
                        ENTITY + '-' + FORMS + "::eventlistener-settings-form-error");
    }

    /**
     * Saves new {@link com.openkoda.model.event.EventListenerEntry} in the database and prepares model and view for the resulting page
     * See also {@link AbstractEventListenerController}
     *
     * @param eventListenerForm
     * @param br
     * @return java.lang.Object
     */
    @PreAuthorize(CHECK_CAN_MANAGE_BACKEND)
    @PostMapping(_NEW_SETTINGS)
    public Object createEventListener(@Valid EventListenerForm eventListenerForm, BindingResult br) {
        debug("[createEventListener]");
        return create(eventListenerForm, br)
                .mav(ENTITY + '-' + FORMS + "::eventlistener-settings-form-success",
                        ENTITY + '-' + FORMS + "::eventlistener-settings-form-error");
    }

    /**
     * Removes {@link com.openkoda.model.event.EventListenerEntry} from the database and prepares the result response
     * See also {@link AbstractEventListenerController}
     *
     * @param listenerId
     * @return java.lang.Object
     */
    @PreAuthorize(CHECK_CAN_MANAGE_BACKEND)
    @PostMapping(value = _ID_REMOVE)
    public Object removeEventListener(@PathVariable(ID) Long listenerId) {
        debug("[removeEventListener] ListenerId: {}", listenerId);
        return remove(listenerId)
                .mav(a -> true, a -> false);
    }

    /**
     * Prepares model and view for the manual event sending functionality available in the Admin panel
     * See also {@link AbstractEventListenerController}
     *
     * @return java.lang.Object
     */
    @PreAuthorize(CHECK_CAN_MANAGE_BACKEND)
    @GetMapping(value = _SEND)
    public Object sendEventListener(){
        debug("[sendEventListener]");
        return chooseEvent()
                .mav(EVENTLISTENER + "-" + SEND);
    }

    /**
     * Prepares model and view for the manual event sending of a particular {@link com.openkoda.model.event.Event} type available in the Admin panel
     * See also {@link AbstractEventListenerController}
     *
     * @param eventType
     * @param br
     * @return java.lang.Object
     */
    @PreAuthorize(CHECK_CAN_MANAGE_BACKEND)
    @PostMapping(value = _SEND)
    public Object chosenEvent(SendEventForm eventType, BindingResult br){
        debug("[chosenEvent]");
            return prepareEvent(eventType, br)
                .mav(ENTITY + '-' + FORMS + "::eventlistener-emit-event",
                        ENTITY + '-' + FORMS + "::eventlistener-choose-event-error");
    }

    /**
     * Emits the manually triggered {@link com.openkoda.model.event.Event} with the DTO data provided with the request
     * This is a functionality available in the Admin panel
     * See also {@link AbstractEventListenerController}
     *
     * @param formData
     * @return
     * @throws IOException
     */
    @PreAuthorize(CHECK_CAN_MANAGE_BACKEND)
    @PostMapping(value = _EMIT, headers = "Accept=application/x-www-form-urlencoded")
    public Object emitFormEvent(@RequestParam  Map<String,String> formData) throws IOException {
        debug("[emitEvent]");
        return emitEvent(formData)
                .mav(ENTITY + '-' + FORMS + "::eventlistener-emit-event-success",
                        ENTITY + '-' + FORMS + "::eventlistener-emit-event-error");
    }
}
