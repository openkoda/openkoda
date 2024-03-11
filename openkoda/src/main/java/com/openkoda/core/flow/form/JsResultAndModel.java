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

package com.openkoda.core.flow.form;

import com.openkoda.core.flow.Flow;
import com.openkoda.core.flow.PageModelMap;
import com.openkoda.core.flow.ResultAndModel;
import com.openkoda.core.form.AbstractOrganizationRelatedEntityForm;

import java.util.Map;

public class JsResultAndModel<R, CP, F extends AbstractOrganizationRelatedEntityForm> extends ResultAndModel<R, CP> {

    public final F form;

    protected JsResultAndModel(PageModelMap model, R result, CP services, Map<String, Object> params, F form) {
        super(model, result, services, params);
        this.form = form;
    }

    //TODO: class to one package up, make this class package
    public static <CP, FP extends AbstractOrganizationRelatedEntityForm> JsResultAndModel constructNew(CP services, Map params, FP form) {
        JsResultAndModel result = new JsResultAndModel(new PageModelMap(), null, services, Flow.initParamsMap(params), form);
        return result;

    }


}
