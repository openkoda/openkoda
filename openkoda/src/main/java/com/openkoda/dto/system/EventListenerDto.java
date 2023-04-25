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

package com.openkoda.dto.system;

import com.openkoda.dto.CanonicalObject;
import com.openkoda.dto.OrganizationRelatedObject;
import com.openkoda.model.event.Consumer;
import com.openkoda.model.event.Event;

public class EventListenerDto implements CanonicalObject, OrganizationRelatedObject {

    public String event;
    public String consumer;
    public String staticData1;
    public String staticData2;
    public String staticData3;
    public String staticData4;
    public Long organizationId;
    //TODO Rule 5.1 All fields in a DTO must be either a simple field (String, numbers, boolean, enum) or other DTO or collection of these
    public Event eventObj;
    //TODO Rule 5.1 All fields in a DTO must be either a simple field (String, numbers, boolean, enum) or other DTO or collection of these
    public Consumer consumerObj;

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getConsumer() {
        return consumer;
    }

    public void setConsumer(String consumer) {
        this.consumer = consumer;
    }

    public String getStaticData1() {
        return staticData1;
    }

    public void setStaticData1(String staticData1) {
        this.staticData1 = staticData1;
    }

    public String getStaticData2() {
        return staticData2;
    }

    public void setStaticData2(String staticData2) {
        this.staticData2 = staticData2;
    }

    public String getStaticData3() {
        return staticData3;
    }

    public void setStaticData3(String staticData3) {
        this.staticData3 = staticData3;
    }

    public String getStaticData4() {
        return staticData4;
    }

    public void setStaticData4(String staticData4) {
        this.staticData4 = staticData4;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Event getEventObj() {
        return eventObj;
    }

    public void setEventObj(Event eventObj) {
        this.eventObj = eventObj;
    }

    public Consumer getConsumerObj() {
        return consumerObj;
    }

    public void setConsumerObj(Consumer consumerObj) {
        this.consumerObj = consumerObj;
    }

    @Override
    public String notificationMessage() {
        return String.format("Event listener on %s forwarded to %s. Static data: %s, %s, %s, %s.", event, consumer, staticData1, staticData2, staticData3, staticData4);
    }

}