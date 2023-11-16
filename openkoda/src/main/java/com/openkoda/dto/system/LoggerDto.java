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

package com.openkoda.dto.system;

import com.openkoda.dto.CanonicalObject;

import java.util.Set;

public class LoggerDto implements CanonicalObject {

    public String bufferSizeField;
    public Set<String> loggingClasses;

    public int getBufferSize() {
        return Integer.parseInt(bufferSizeField);
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSizeField = String.valueOf(bufferSize);
    }

    public String getBufferSizeField() {
        return bufferSizeField;
    }

    public void setBufferSizeField(String bufferSizeField) {
        this.bufferSizeField = bufferSizeField;
    }

    public Set<String> getLoggingClasses() {
        return loggingClasses;
    }

    //TODO Rule 5.4: DTO must not have methods that change its state (constructors are allowed)
    public LoggerDto setLoggingClasses(Set<String> loggingClasses) {
        this.loggingClasses = loggingClasses;
        return this;
    }

    @Override
    public String notificationMessage() {
        return String.format("Logging config. Buffer: %s. No of classes: %d.", bufferSizeField, loggingClasses.size());
    }
}