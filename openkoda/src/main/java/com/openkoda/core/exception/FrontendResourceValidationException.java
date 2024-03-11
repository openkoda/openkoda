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

package com.openkoda.core.exception;


import com.openkoda.core.flow.HttpStatusException;
import com.openkoda.model.component.FrontendResource;
import org.springframework.http.HttpStatus;

//TODO: check why we can not use ValidationException instead
/**
 * Exception used for validation of {@link FrontendResource}
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 * 
 */
public class FrontendResourceValidationException extends HttpStatusException {
    public final boolean result;
    public final String suggestion;

    /**
     * <p>Constructor for FrontendResourceValidationException.</p>
     *
     * @param result a boolean.
     * @param suggestion a {@link java.lang.String} object.
     */
    public FrontendResourceValidationException(boolean result, String suggestion) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, suggestion);
        this.result = result;
        this.suggestion = suggestion;
    }

    /**
     * <p>isResult.</p>
     *
     * @return a boolean.
     */
    public boolean isResult() {
        return result;
    }

    /**
     * <p>Getter for the field <code>suggestion</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getSuggestion() {
        return suggestion;
    }
}
