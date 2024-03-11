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
import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.form.SchedulerForm;
import com.openkoda.model.component.Scheduler;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.validation.BindingResult;

/**
 * <p>Controller that provides actual {@link Scheduler} related functionality for different types of access
 * (eg. API, HTML)</p>
 * <p>Implementing classes should take over http binding and forming a result whereas this controller should take care
 * of actual implementation</p>
 * See also {@link SchedulerControllerHtml}
 *
 * @author Martyna Litkowska (mlitkowska@stratoflow.com)
 * @since 2019-03-20
 */
public class AbstractSchedulerController extends ComponentProvider implements HasSecurityRules {

    /**
     * Retrieves {@link Scheduler} page from the database for the parameters provided.
     *
     * @param schedulerSearchTerm
     * @param schedulerSpecification {@link Specification} for {@link Scheduler} retrieval
     * @param schedulerPageable {@link Pageable} for {@link Scheduler} page search
     * @return com.openkoda.core.flow.PageModelMap
     */
    protected PageModelMap findSchedulersFlow(
            String schedulerSearchTerm,
            Specification<Scheduler> schedulerSpecification,
            Pageable schedulerPageable) {
        debug("[findSchedulersFlow] search {}", schedulerSearchTerm);
        return Flow.init()
                .thenSet(schedulerPage, a -> repositories.secure.scheduler.search(schedulerSearchTerm, null, schedulerSpecification, schedulerPageable))
                .execute();
    }

    /**
     * Retrieves th {@link Scheduler} entity from the database for the provided ID and prepares {@link SchedulerForm} for it.
     *
     * @param schedulerId
     * @return com.openkoda.core.flow.PageModelMap
     */
    protected PageModelMap find(long schedulerId) {
        debug("[find] schedulerId {}", schedulerId);
        return Flow.init()
                .thenSet(schedulerEntity, a -> repositories.secure.scheduler.findOne(schedulerId))
                .thenSet(schedulerForm, a -> new SchedulerForm(null, a.result))
                .execute();
    }


    /**
     * Validates data in the {@link SchedulerForm} provided and saves a new {@link Scheduler} record in the database.
     * For a successful save, it registers the scheduler in the running application.
     * See also {@link com.openkoda.core.service.event.ClusterEventSenderService}
     *
     * @param schedulerFormData {@link SchedulerForm}
     * @param br
     * @return com.openkoda.core.flow.PageModelMap
     */
    protected PageModelMap create(SchedulerForm schedulerFormData, BindingResult br) {
        debug("[createScheduler]");
        return Flow.init(schedulerForm, schedulerFormData)
                .then(a -> services.validation.validateAndPopulateToEntity(schedulerFormData, br,new Scheduler()))
                .thenSet(schedulerEntity, a -> repositories.unsecure.scheduler.saveAndFlush(a.result))
                .then(a -> services.componentExport.exportToFileIfRequired(a.result))
                .then(a -> services.scheduler.loadClusterAware(a.result.getId()))
                .thenSet(schedulerForm, a -> new SchedulerForm())
                .execute();
    }

    /**
     * Retrieves the {@link Scheduler} entity from the database for the provided ID.
     * It then validates the contents of {@link SchedulerForm} and populates data to the entity.
     * After updating the database record successfully it triggers reload of the updated scheduler running in the application.
     * See also {@link com.openkoda.core.service.event.ClusterEventSenderService}
     *
     * @param schedulerId
     * @param schedulerFormData {@link SchedulerForm}
     * @param br
     * @return com.openkoda.core.flow.PageModelMap
     */
    protected PageModelMap update(long schedulerId, SchedulerForm schedulerFormData, BindingResult br) {
        debug("[updateScheduler] schedulerId: {}", schedulerId);
        return Flow.init().init(schedulerForm, schedulerFormData)
                .then(a -> repositories.unsecure.scheduler.findOne(schedulerId))
                .then(a -> services.validation.validateAndPopulateToEntity(schedulerFormData, br,a.result))
                .then(a -> repositories.unsecure.scheduler.saveAndFlush(a.result))
                .then(a -> services.componentExport.exportToFileIfRequired(a.result))
                .then(a -> services.scheduler.reloadClusterAware(a.result.getId()))
                .execute();
    }

    /**
     * Removes {@link Scheduler} with a provided ID from the database and removes it from currently running schedulers in the application.
     * See also {@link com.openkoda.core.service.event.ClusterEventSenderService}
     *
     * @param schedulerId
     * @return com.openkoda.core.flow.PageModelMap
     */
    protected PageModelMap removeScheduler(long schedulerId) {
        debug("[removeScheduler] schedulerId: {}", schedulerId);
        return Flow.init(schedulerId)
                .then(a -> repositories.secure.scheduler.findOne(schedulerId))
                .then(a -> services.componentExport.removeExportedFilesIfRequired(a.result))
                .then(a -> repositories.unsecure.scheduler.deleteOne(schedulerId))
                .then(a -> services.scheduler.removeClusterAware(schedulerId))
                .execute();
    }
}
