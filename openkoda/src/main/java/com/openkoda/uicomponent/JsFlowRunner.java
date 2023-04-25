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

package com.openkoda.uicomponent;

import com.openkoda.core.flow.Flow;
import com.openkoda.core.flow.PageModelMap;
import com.openkoda.core.flow.form.JsFlow;
import com.openkoda.core.form.AbstractOrganizationRelatedEntityForm;
import com.openkoda.uicomponent.live.LiveComponentProvider;
import com.openkoda.uicomponent.preview.PreviewComponentProvider;
import jakarta.inject.Inject;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;
import org.springframework.stereotype.Component;
import reactor.util.function.Tuples;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.openkoda.controller.common.PageAttributes.*;

@Component
public class JsFlowRunner {

    private Context.Builder contextBuilder = Context.newBuilder("js")
            .allowHostAccess(HostAccess.ALL)
            .allowAllAccess(true)
            .allowHostClassLoading(true)
            .allowHostClassLookup(className -> true);

    @Inject
    private LiveComponentProvider componentProvider;

    @Inject
    private PreviewComponentProvider previewComponentProvider;

    private Flow evaluateJsFlow(String jsFlow, Flow initializedFlow) {
        Context c = contextBuilder.build();
        Value b = c.getBindings("js");
        b.putMember("flow", initializedFlow);
        b.putMember("dateNow", (Supplier<LocalDate>) () -> LocalDate.now());
        b.putMember("dateTimeNow", (Supplier<LocalDateTime>) () -> LocalDateTime.now());
        b.putMember("parseInt", (Function<String, Integer>) s -> Integer.parseInt(s));
        b.putMember("parseLong", (Function<String, Long>) s -> Long.parseLong(s));
        b.putMember("parseDate", (Function<String, LocalDate>) s -> LocalDate.parse(s));
        String finalScript = "let result = " + jsFlow + ";\nresult";

        Flow result = c.eval("js", finalScript).as(initializedFlow.getClass());
        return result;
    }

    private PageModelMap executeFlow(Flow f) {
        try {
            return f.execute();
        } catch (PolyglotException e) {
            Throwable hostException = e.asHostException();
            if (RuntimeException.class.isAssignableFrom(hostException.getClass())) {
                throw (RuntimeException)hostException;
            }
            throw e;
        }
    }

    public PageModelMap runPreviewFlow(String jsFlow, Map<String, String> params, Long organizationId, long userId, AbstractOrganizationRelatedEntityForm form) {
        Flow f = JsFlow.init(previewComponentProvider, params, form)
                .thenSet(organizationEntityId, userEntityId, a -> Tuples.of(organizationId, userId));

        try {
            return executeFlow(evaluateJsFlow(jsFlow, f));
        } catch (PolyglotException e) {
            PageModelMap pageModelMap = new PageModelMap();
            pageModelMap.put(errorMessage, e.getMessage());
            return pageModelMap;
        }
    }

    public PageModelMap runLiveFlow(String jsFlow, Map<String, String> params, Long organizationId, long userId, AbstractOrganizationRelatedEntityForm form) {
        Flow f = JsFlow.init(componentProvider, params, form)
                .thenSet(organizationEntityId, userEntityId, a -> Tuples.of(organizationId, userId));

        return executeFlow(evaluateJsFlow(jsFlow, f));
    }

}
