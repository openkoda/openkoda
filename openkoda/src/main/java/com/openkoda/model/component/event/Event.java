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

package com.openkoda.model.component.event;

import org.apache.commons.lang3.StringUtils;

/**
 * Helper Event object to perform mapping between {@link com.openkoda.form.EventListenerForm} adn {@link EventListenerEntry}
 *
 * @author Martyna Litkowska (mlitkowska@stratoflow.com)
 * @since 2019-03-12
 */
public class Event {

    private String eventClassName;
    private String eventName;
    private String eventObjectType;

    public Event(String eventString) {
        String[] eventSplit = eventString.split(",");
        this.eventClassName = eventSplit[0];
        this.eventName = eventSplit[1];
        this.eventObjectType = eventSplit[2];
    }

    public String getEventClassName() {
        return eventClassName;
    }

    public void setEventClassName(String eventClassName) {
        this.eventClassName = eventClassName;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventObjectType() {
        return eventObjectType;
    }

    public void setEventObjectType(String eventObjectType) {
        this.eventObjectType = eventObjectType;
    }

    @Override
    public String toString() {
        return "Event[" + eventClassName + ',' + eventName + ',' + eventObjectType + ']';
    }

    public String getEventString(){
        return StringUtils.join(new String[] {eventClassName, eventName, eventObjectType}, ",");
    }

}
