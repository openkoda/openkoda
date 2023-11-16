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

import com.openkoda.form.SchedulerForm;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static com.openkoda.core.controller.generic.AbstractController.*;

/**
 * <p>The controller for server-side generated html actions extending the {@link AbstractSchedulerController} which
 * does the actual logic.</p>
 * <p>General contract is: resolve HTTP bindings, delegate work to {@link AbstractSchedulerController} and provide
 * ModelAndView</p>
 * See also {@link AbstractSchedulerController}
 *
 * @author Martyna Litkowska (mlitkowska@stratoflow.com)
 * @since 2019-03-20
 */
@RestController
@RequestMapping(_HTML + _SCHEDULER)
public class SchedulerControllerHtml extends AbstractSchedulerController {

    /**
     * Prepares model and view to display all {@link com.openkoda.model.event.Scheduler} page
     * See also {@link AbstractSchedulerController}
     *
     * @param pageable
     * @param search
     * @return java.lang.Object
     */
    @PreAuthorize(CHECK_CAN_READ_BACKEND)
    @GetMapping(value = _ALL)
    public Object getAll(
            @Qualifier("scheduler") Pageable pageable,
            @RequestParam(required = false, defaultValue = "", name = "scheduler_search") String search) {
        debug("[getAll] search {}", search);
        return findSchedulersFlow(search, null, pageable)
                .mav(SCHEDULER + "-" + ALL);
    }

    /**
     * Prepares model and view for {@link com.openkoda.model.event.Scheduler} settings page
     * See also {@link AbstractSchedulerController}
     *
     * @param schedulerId
     * @return java.lang.Object
     */
    @PreAuthorize(CHECK_CAN_READ_BACKEND)
    @GetMapping(value = _ID + _SETTINGS)
    public Object settings(@PathVariable(ID) Long schedulerId) {
        debug("[settings] schedulerId {}", schedulerId);
        return find(schedulerId)
                .mav(SCHEDULER + "-settings");
    }

    /**
     * Prepares model and view for the new {@link com.openkoda.model.event.Scheduler} configuration page
     * See also {@link AbstractSchedulerController}
     *
     * @return java.lang.Object
     */
    @PreAuthorize(CHECK_CAN_MANAGE_BACKEND)
    @GetMapping(_NEW_SETTINGS)
    public Object newScheduler() {
        debug("[newScheduler]");
        return find(-1L)
                .mav(SCHEDULER + "-settings");
    }

    /**
     * Triggers update of the {@link com.openkoda.model.event.Scheduler} and prepares model and view for the result page
     * See also {@link AbstractSchedulerController}
     *
     * @param schedulerId
     * @param schedulerForm
     * @param br
     * @return java.lang.Object
     */
    @PreAuthorize(CHECK_CAN_MANAGE_BACKEND)
    @PostMapping(value = _ID + _SETTINGS)
    public Object updateScheduler(@PathVariable(ID) Long schedulerId, @Valid SchedulerForm schedulerForm, BindingResult br) {
        debug("[updateScheduler] schedulerId {}", schedulerId);
        return update(schedulerId, schedulerForm, br)
                .mav(ENTITY + '-' + FORMS + "::scheduler-settings-form-success",
                        ENTITY + '-' + FORMS + "::scheduler-settings-form-error");
    }

    /**
     * Saves new {@link com.openkoda.model.event.Scheduler} in the database and prepares model and view for the resulting page
     * See also {@link AbstractSchedulerController}
     *
     * @param schedulerForm
     * @param br
     * @return
     */
    @PreAuthorize(CHECK_CAN_MANAGE_BACKEND)
    @PostMapping(_NEW_SETTINGS)
    public Object createScheduler(@Valid SchedulerForm schedulerForm, BindingResult br) {
        debug("[createScheduler]");
        return create(schedulerForm, br)
                .mav(ENTITY + '-' + FORMS + "::scheduler-settings-form-success",
                        ENTITY + '-' + FORMS + "::scheduler-settings-form-error");
    }

    /**
     * Removes {@link com.openkoda.model.event.Scheduler} from the database and prepares the result response
     * See also {@link AbstractSchedulerController}
     *
     * @param schedulerId
     * @return
     */
    @PreAuthorize(CHECK_CAN_MANAGE_BACKEND)
    @PostMapping(value = _ID_REMOVE)
    public Object remove(@PathVariable(ID) Long schedulerId) {
        debug("[remove] schedulerId {}", schedulerId);
        return removeScheduler(schedulerId)
                .mav(a -> true, a -> false);
    }

}
