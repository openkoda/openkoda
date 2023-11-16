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
import com.openkoda.core.flow.TransactionalExecutor;
import com.openkoda.core.form.AbstractOrganizationRelatedEntityForm;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class JsFlow<I, O, CP> extends Flow<I, O, CP> {

    private final AbstractOrganizationRelatedEntityForm form;

    protected JsFlow(Map<String, Object> params, CP services, Function<ResultAndModel<I, CP>, O> f, Supplier<TransactionalExecutor> transactionalExecutorProvider, Consumer<Function> onThen, AbstractOrganizationRelatedEntityForm form) {
        super(params, services, f, transactionalExecutorProvider, onThen);
        this.form = form;
    }

    @Override
    protected <II, IO, ICP> Flow<II, IO, ICP> constructFlow(Map<String, Object> params, ICP services, Function<ResultAndModel<II, ICP>, IO> f, Supplier<TransactionalExecutor> transactionalExecutorProvider, Consumer<Function> onThen) {
        return new JsFlow(params, services, f, transactionalExecutorProvider, onThen, form);
    }

    @Override
    protected <IR, ICP> ResultAndModel<IR, ICP> constructResultAndModel(PageModelMap model, IR result, ICP services, Map<String, Object> params) {
        return new JsResultAndModel<>(model, result, services, params, form);
    }

    public static <A, CP> Flow<A, A, CP> init(CP services, Map params, AbstractOrganizationRelatedEntityForm form) {
        return new JsFlow<>(initParamsMap(params), services, a->a.result, null, null, form);
    }

}
