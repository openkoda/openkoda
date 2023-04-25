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

package com.openkoda.form;

import com.openkoda.core.form.AbstractOrganizationRelatedEntityForm;
import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import com.openkoda.dto.system.EventListenerDto;
import com.openkoda.model.event.Consumer;
import com.openkoda.model.event.Event;
import com.openkoda.model.event.EventListenerEntry;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.BindingResult;

public class EventListenerForm extends AbstractOrganizationRelatedEntityForm<EventListenerDto, EventListenerEntry> implements LoggingComponentWithRequestId {


    public EventListenerForm() {
        super(FrontendMappingDefinitions.eventListenerForm);
    }

    public EventListenerForm(Long organizationId, EventListenerEntry entity) {
        super(organizationId, new EventListenerDto(), entity, FrontendMappingDefinitions.eventListenerForm);
    }

    @Override
    public EventListenerForm populateFrom(EventListenerEntry entity) {
        dto.consumer = entity.getConsumerString();
        dto.event = entity.getEventString();
        dto.staticData1 = entity.getStaticData1();
        dto.staticData2 = entity.getStaticData2();
        dto.staticData3 = entity.getStaticData3();
        dto.staticData4 = entity.getStaticData4();
        dto.organizationId = entity.getOrganizationId();
        return this;
    }

    private String getConsumerOrEventParam(String allParams, int n)
    {
        String[] parameters = allParams.split(",");
        assert(parameters.length > n);
        return parameters[n];
    }

    @Override
    protected EventListenerEntry populateTo(EventListenerEntry entity) {
        entity.setConsumerClassName(getSafeValue(entity.getConsumerClassName(), CONSUMER_, (s -> getConsumerOrEventParam((String) s,0))));
        entity.setConsumerMethodName(getSafeValue(entity.getConsumerMethodName(), CONSUMER_, (s -> getConsumerOrEventParam((String) s,1))));
        entity.setConsumerParameterClassName(getSafeValue(entity.getConsumerParameterClassName(), CONSUMER_, (s -> getConsumerOrEventParam((String) s,2))));
        entity.setStaticData1(getSafeValue(entity.getStaticData1(), STATIC_DATA_1_, nullOnEmpty));
        entity.setStaticData2(getSafeValue(entity.getStaticData2(), STATIC_DATA_2_, nullOnEmpty));
        entity.setStaticData3(getSafeValue(entity.getStaticData3(), STATIC_DATA_3_, nullOnEmpty));
        entity.setStaticData4(getSafeValue(entity.getStaticData4(), STATIC_DATA_4_, nullOnEmpty));
        entity.setEventClassName(getSafeValue(entity.getEventClassName(), EVENT_, (s -> getConsumerOrEventParam((String) s,0))));
        entity.setEventName(getSafeValue(entity.getEventName(), EVENT_, (s -> getConsumerOrEventParam((String) s,1))));
        entity.setEventObjectType(getSafeValue(entity.getEventObjectType(), EVENT_, (s -> getConsumerOrEventParam((String) s,2))));
        entity.setOrganizationId(getSafeValue(entity.getOrganizationId(), ORGANIZATION_ID_));
        return entity;
    }

    @Override
    public EventListenerForm validate(BindingResult br) {

        if (dto.event == "") {
            br.rejectValue("dto.event", "not.empty", defaultErrorMessage);
        } else {
            dto.eventObj = new Event(dto.event);
        }
        if (dto.consumer == null) {
            br.rejectValue("dto.consumer", "not.empty", defaultErrorMessage);
        } else {
            dto.consumerObj = new Consumer(dto.consumer);
        }
        if (dto.consumer != null && dto.event != null) {
            String eventObjectClassName = StringUtils.substringAfterLast(dto.event, ",");
            String consumerObjectClassName = dto.consumer.split(",")[2];
            if (!isPerfectMatch(eventObjectClassName, consumerObjectClassName)) {
                br.rejectValue("dto.consumer", "incompatible.consumer", defaultErrorMessage);
                br.rejectValue("dto.event", "incompatible.consumer", defaultErrorMessage);
            }
        }
        if (dto.consumer != null && !(dto.consumerObj.getNumberOfStaticParams() >= 1) && !StringUtils.isBlank
                (dto.staticData1)) {
            br.rejectValue("dto.staticData1", "not.valid", defaultErrorMessage);
        }
        if (dto.consumer != null && !(dto.consumerObj.getNumberOfStaticParams() >= 2) && !StringUtils.isBlank
                (dto.staticData2)) {
            br.rejectValue("dto.staticData2", "not.valid", defaultErrorMessage);
        }
        if (dto.consumer != null && !(dto.consumerObj.getNumberOfStaticParams() >= 3) && !StringUtils.isBlank
                (dto.staticData3)) {
            br.rejectValue("dto.staticData3", "not.valid", defaultErrorMessage);
        }
        if (dto.consumer != null && !(dto.consumerObj.getNumberOfStaticParams() == 4) && !StringUtils.isBlank
                (dto.staticData4)) {
            br.rejectValue("dto.staticData4", "not.valid", defaultErrorMessage);
        }

        if (dto.consumer != null && dto.consumerObj.getNumberOfStaticParams() >= 1 && StringUtils.isBlank(dto.staticData1)) {
            br.rejectValue("dto.staticData1", "not.empty", defaultErrorMessage);
        }
        if (dto.consumer != null && dto.consumerObj.getNumberOfStaticParams() >= 2 && StringUtils.isBlank(dto.staticData2)) {
            br.rejectValue("dto.staticData2", "not.empty", defaultErrorMessage);
        }
        if (dto.consumer != null && dto.consumerObj.getNumberOfStaticParams() >= 3 && StringUtils.isBlank(dto.staticData3)) {
            br.rejectValue("dto.staticData3", "not.empty", defaultErrorMessage);
        }
        if (dto.consumer != null && dto.consumerObj.getNumberOfStaticParams() == 4 && StringUtils.isBlank(dto.staticData4)) {
            br.rejectValue("dto.staticData4", "not.empty", defaultErrorMessage);
        }
        return this;
    }


    public boolean isPerfectMatch(String eventClassName, String consumerClassName) {
        try {
            Class<?> consumerObjectClass = Class.forName(consumerClassName);
            Class<?> eventObjectClass = Class.forName(eventClassName);
            return consumerObjectClass.isAssignableFrom(eventObjectClass);
        } catch (Exception e) {
            error(e, "Error when trying to find classes for EventListener: {} {}", eventClassName, consumerClassName);
            throw new RuntimeException(e);
        }
    }
}
