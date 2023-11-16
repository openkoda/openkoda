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

package com.openkoda.service.export.dto;

public class EventListenerEntryConversionDto {

    private String consumerClassName;
    private String consumerMethodName;
    private String consumerParameterClassName;
    private String eventClassName;
    private String eventName;
    private String eventObjectType;
    private String indexString;
    private String staticData1;
    private String staticData2;
    private String staticData3;
    private String staticData4;

    public String getConsumerClassName() {
        return consumerClassName;
    }

    public void setConsumerClassName(String consumerClassName) {
        this.consumerClassName = consumerClassName;
    }

    public String getConsumerMethodName() {
        return consumerMethodName;
    }

    public void setConsumerMethodName(String consumerMethodName) {
        this.consumerMethodName = consumerMethodName;
    }

    public String getConsumerParameterClassName() {
        return consumerParameterClassName;
    }

    public void setConsumerParameterClassName(String consumerParameterClassName) {
        this.consumerParameterClassName = consumerParameterClassName;
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

    public String getIndexString() {
        return indexString;
    }

    public void setIndexString(String indexString) {
        this.indexString = indexString;
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
}
