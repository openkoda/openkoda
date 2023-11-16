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

package com.openkoda.form;

import com.openkoda.core.form.AbstractOrganizationRelatedEntityForm;
import com.openkoda.core.form.FrontendMappingDefinition;
import com.openkoda.dto.system.SchedulerDto;
import com.openkoda.model.event.Scheduler;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.validation.BindingResult;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Form class which allows to generate html userForm on the basis of defined userForm fields.
 * It is used for creating and editing {@link Scheduler} entities.
 *
 * @author Martyna Litkowska (mlitkowska@stratoflow.com)
 * @since 2019-03-20
 */
public class SchedulerForm extends AbstractOrganizationRelatedEntityForm<SchedulerDto, Scheduler> {

    public SchedulerForm() {
        super(FrontendMappingDefinitions.schedulerForm);
    }

    public SchedulerForm(Long organizationId, Scheduler entity) {
        super(organizationId, new SchedulerDto(), entity, FrontendMappingDefinitions.schedulerForm);
    }

    public SchedulerForm(Long organizationId, SchedulerDto dto, Scheduler entity, FrontendMappingDefinition frontendMappingDefinition) {
        super(organizationId, dto, entity, frontendMappingDefinition);
    }

    @Override
    public SchedulerForm populateFrom(Scheduler entity) {
        dto.cronExpression = entity.getCronExpression();
        dto.eventData = entity.getEventData();
        dto.organizationId = entity.getOrganizationId();
        dto.onMasterOnly = entity.isOnMasterOnly();
        return this;
    }

    @Override
    protected Scheduler populateTo(Scheduler entity) {

        entity.setCronExpression(getSafeValue(entity.getCronExpression(), CRON_EXPRESSION_));
        entity.setEventData(getSafeValue(entity.getEventData(), EVENT_DATA_));
        entity.setOrganizationId(getSafeValue(entity.getOrganizationId(), ORGANIZATION_ID_));
        entity.setOnMasterOnly(getSafeValue(entity.isOnMasterOnly(), ON_MASTER_ONLY_));

        return entity;
    }

    @Override
    public SchedulerForm validate(BindingResult br) {
        if(isBlank(dto.cronExpression)) { br.rejectValue("dto.cronExpression", "not.empty", defaultErrorMessage); }
        if(isBlank(dto.eventData)) { br.rejectValue("dto.eventData", "not.empty", defaultErrorMessage); }
        if(!CronSequenceGenerator.isValidExpression(dto.cronExpression)) { br.rejectValue("dto.cronExpression", "not" +
                        ".valid",
                defaultErrorMessage); }
        return this;
    }

}
