/*
MIT License

Copyright (c) 2014-2022, Codedose CDX Sp. z o.o. Sp. K. <stratoflow.com>

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

package com.openkoda.core.flow;

import java.util.Map;

/**
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 * @since 2016-09-14
 */
public class ResultAndModel<R, CP> {
    private static int objCount = 0;
    public final PageModelMap model;
    public final R result;
    public final CP services;
    public final Map<String, Object> params;
    protected ResultAndModel(PageModelMap model, R result, CP services, Map<String, Object> params) {
        this.result = result;
        this.model = model;
        this.services = services;
        this.params = params;
        objCount++;
    }


    /**
     * Executes the stack and returns object depending on validation result
     *
     * @param forSuccess         object provider used to return object when validation passes
     * @param forValidationError object provider used to return object when validation fails
     * @return object provided by one of given providers
     */
    public <O> O mav(ResultAndModelFunction<O, R> forSuccess, ResultAndModelFunction<O, R> forValidationError) {
        return model.get(BasePageAttributes.isError) ? forValidationError.getResult(this) : forSuccess.getResult(this);
    }

    /**
     * Executes the stack and returns object regardless of validation result
     *
     * @param forSuccessAndError object provider used to return object regardless of validation result
     * @return object provided by one of given providers
     */
    public <O> O mav(ResultAndModelFunction<O, R> forSuccessAndError) {
        return mav(forSuccessAndError, forSuccessAndError);
    }

    public static int getObjectCount() {
        return objCount;
    }
}
